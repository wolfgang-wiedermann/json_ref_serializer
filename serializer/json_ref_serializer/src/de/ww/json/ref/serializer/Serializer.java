package de.ww.json.ref.serializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import de.ww.json.ref.serializer.annotations.JustReference;
import de.ww.json.ref.serializer.annotations.RestPath;
import de.ww.json.ref.serializer.exceptions.SerializerException;

/**
 * JSON-Serialisierer, der es ermöglicht Beziehungen zwischen Objekten
 * wahlweise direkt in den JSON-String zu serialisieren (standard anderer Serialisierer)
 * oder als Hyperlink-Referenz (zum Nachladen) anzugeben.
 * 
 * siehe z. B. http://en.wikipedia.org/wiki/HATEOAS
 * 
 * 
 * @author wiw39784
 *
 */
public class Serializer {

	public final static String OBJECT_OPEN = "{"
			, OBJECT_CLOSE = "}"
			, ARRAY_OPEN = "["
			, ARRAY_CLOSE = "]"
			, STRING_DELIMITER = "'"
			, NAME_DELIMITER = ":"
			, FIELD_DELIMITER = ",";
	
	/**
	 * Wandelt ein Objekt in einen JSON-String um ...
	 * @param data
	 * @return
	 * @throws SerializerException 
	 */
	public static String serialize(Object data) throws SerializerException {
		StringBuffer buffer = new StringBuffer();		
		if(isList(data)) {
			writeList(buffer, data, false);
		} else {
			writeObject(buffer, data);
		}		
		return buffer.toString();
	}
	
	/**
	 * Writes a Java-Object into a JSON-String
	 * 
	 * @param buffer for the JSON-String
	 * @param data   Object to be serialized to JSON
	 * @throws SerializerException
	 */
	private static void writeObject(StringBuffer buffer, Object data) throws SerializerException {
		Class<?> type = data.getClass();
		Method methods[] = type.getMethods();
		buffer.append(OBJECT_OPEN);
		for(Method m : methods) {
			if(isScalarGetter(m)) {				
				writeScalar(buffer, data, m);
			} else if(isListGetter(m)) {
				writeList(buffer, data, m);
			}
		}
		buffer.append(OBJECT_CLOSE);
	}
	
	/**
	 * Serialisiert Attribute mit Listen
	 * 
	 * TODO: ein geeignetes WriteList für Listen mit Referenzen schreiben.
	 * 
	 * @param buffer
	 * @param data
	 * @param m
	 * @throws SerializerException 
	 */
	private static void writeList(StringBuffer buffer, Object data, Method m) throws SerializerException {
		String fieldName = getNormalizedGetterString(m);
		Object list = get(data, m);
		
		buffer.append(STRING_DELIMITER);
		buffer.append(fieldName);
		buffer.append(STRING_DELIMITER);
		buffer.append(NAME_DELIMITER);
		writeList(buffer, list, m.isAnnotationPresent(JustReference.class));
		buffer.append(FIELD_DELIMITER);
		
	}
	
	/**
	 * Schreiben der eigentlichen Liste
	 * @param buffer
	 * @param list
	 * @throws SerializerException
	 */
	private static void writeList(StringBuffer buffer, Object list, boolean justReference) throws SerializerException {
		buffer.append(ARRAY_OPEN);
		if(list != null) {
			
			if(list.getClass().isArray()) {
				list = arrayToListObject(list);
			}
			Iterable<?> castedList = (Iterable<?>)list;
			
			for(Object o : castedList) {
				if(o == null) {
					buffer.append("null");
				} else if(o.getClass().isPrimitive()) {
					buffer.append(""+o);
				} else if(TypeSerializerRepository.getInstance().isSerializerAvailableFor(o.getClass())) {
					buffer.append(TypeSerializerRepository.getInstance().serialize(o));						
				} else {
					if(justReference) {
						buffer.append(writeReference(o));
					} else {
						buffer.append(serialize(o));
					}
				}
				buffer.append(FIELD_DELIMITER);
			}
		} 
		buffer.append(ARRAY_CLOSE);
	}

	/**
	 * Einen Eintrag für ein Skalar-Feld schreiben
	 * @param buffer
	 * @param data Objekt zu dem die Get-Methode gehört
	 * @param m Get-Methode
	 * @throws SerializerException 
	 */
	private static void writeScalar(StringBuffer buffer, Object data, Method m) throws SerializerException {
		String fieldName = getNormalizedGetterString(m);
		
		Object value = getValue(data, m);
		
		buffer.append(STRING_DELIMITER);
		buffer.append(fieldName);
		buffer.append(STRING_DELIMITER);
		buffer.append(NAME_DELIMITER);		
		buffer.append(value);		
		buffer.append(FIELD_DELIMITER);
	}
	
	/**
	 * Ermittelt den Wert eines Feldes in passender Darstellung für den JSON-String
	 * @param data
	 * @param m
	 * @return
	 * @throws SerializerException 
	 */
	private static Object getValue(Object data, Method m) throws SerializerException {
		Object value = get(data, m);	
		if(value == null) {
			return null;
		} else if(m.getReturnType().isPrimitive() 
				|| m.getReturnType().equals(String.class)) {
			// Primitive Typen und Strings			
			value = STRING_DELIMITER+value+STRING_DELIMITER;
		} else if(TypeSerializerRepository.getInstance().isSerializerAvailableFor(m.getReturnType())) {
			// Spezielle Basistypen wie java.lang.Date			
			value = TypeSerializerRepository.getInstance().serialize(value);			
		} else {
			// Alle anderen komplexen Typen
			Object result = get(data, m);	 
			if(m.isAnnotationPresent(JustReference.class)) {
				value = writeReference(result);			
			} else {				
				value = serialize(result);				
			}
		}
		return value;
	}

	/**
	 * Schreibt ein Referenz-Objekt anstelle des eigentlichen JSON-Objekts
	 * 
	 * TODO: So funktionierts aber bei den Listen noch nicht ...
	 * 
	 * @param result
	 * @return
	 * @throws SerializerException 
	 */
	private static String writeReference(Object result) throws SerializerException {
		
		if(!result.getClass().isAnnotationPresent(RestPath.class))
			throw new IllegalStateException("Referenzen sind nur für Entities mit RestPath-Annotation zulässig");
		
		try {
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(OBJECT_OPEN);
			writeReferenceUrlAttribute(buffer, result);					
			buffer.append(FIELD_DELIMITER);
			writeReferenceGetMethod(buffer, result);
			buffer.append(OBJECT_CLOSE);			
			return buffer.toString();
			
		} catch(Exception ex) {
			throw new SerializerException(ex);
		}
	}
	
	/**
	 * Schreibt das URL-Attribut in die Referenz 
	 * (Sollte nur aus writeReference aufgerufen werden!)
	 * 
	 * TODO: Diese Methode ist nur ein einfacher Dummy, die reale Implementierung muss beliebige {}-Parameter können 
	 * 
	 * @param buffer
	 * @param result
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static void writeReferenceUrlAttribute(StringBuffer buffer, Object result) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		buffer.append("'url':'");
		// TODO: in Zukunft noch alle möglichen enthaltenen {}-Parameter ersetzen!
		String urlTemplate = result.getClass().getAnnotation(RestPath.class).value();
		String url = urlTemplate.replaceAll("\\{id\\}", result.getClass().getMethod("getId").invoke(result).toString());
		buffer.append(url);
		buffer.append("'");
	}
	
	/**
	 * Schreibt in den buffer eine Methode zum Laden des Objekt mit der URL this.url
	 * @param buffer
	 * @param result
	 */
	private static void writeReferenceGetMethod(StringBuffer buffer, Object result) {
		// TODO: hier noch eine richtige Lade-Funktion generieren...
		buffer.append("'get':function(successHandler, errorHandler) { alert(this.url); }");
	}

	/**
	 * Prüft, ob eine Methode ein gültiger Getter ist.
	 * @param m
	 * @return
	 */
	private static boolean isGetter(Method m) {
		return m.getName().startsWith("get") 
				&& !(m.getReturnType().equals(Void.class) || m.getReturnType().equals(void.class))
				&& !m.getName().equals("getClass")
				&& m.getParameterCount() == 0;
	}
	
	/**
	 * Ermittelt ob die Methode getter zu einem Skalar ist
	 * @param m
	 * @return
	 */
	private static boolean isScalarGetter(Method m) {
		return isGetter(m) 
				&& !(Iterable.class.isAssignableFrom(m.getReturnType()) || m.getReturnType().isArray());
	}
	
	/**
	 * Ermittelt ob die Methode getter zu einer Liste ist
	 * @param m
	 * @return
	 */
	private static boolean isListGetter(Method m) {
		return isGetter(m) 
				&& (Iterable.class.isAssignableFrom(m.getReturnType()) || m.getReturnType().isArray());
	}
	
	/**
	 * Prüft, ob ein Objekt ein Listentyp ist (liefert im Falle o == null false)
	 * @param o
	 * @return
	 */
	private static boolean isList(Object o) {
		if(o != null)
			return o.getClass().isArray() || Iterable.class.isAssignableFrom(o.getClass());
		else 
			return false;
	}
	
	/**
	 * Macht aus dem Namen einer get*-Methode einen Feld-Namen
	 * @param m
	 * @return
	 */
	private static String getNormalizedGetterString(Method m) {
		if(!isGetter(m)) 
			throw new IllegalStateException("Methode ist kein gültiger Getter");
		
		String name = m.getName().replaceFirst("get", "");
		name = name.toLowerCase();
		
		return name;
	}
	
	/**
	 * Einen Getter aufrufen und mglw. auftretende Fehler als SerializerException verpacken
	 * @param data Objekt, an dem die Methode aufgerufen werden soll
	 * @param m    Methode, die aufgerufen werden soll
	 * @return
	 * @throws SerializerException
	 */
	private static Object get(Object data, Method m) throws SerializerException {
		Object result;
		try {
			result = m.invoke(data);
			return result;
		} catch (Exception e) {
			throw new SerializerException(e);
		} 	
	}
	
	/**
	 * 
	 * @param data
	 * @return
	 */
	private static List arrayToListObject(Object data) throws SerializerException {
		if(data == null) return null;
		if(!data.getClass().isArray()) throw new SerializerException("Es soll ein Array, das kein Array is in eine Liste umgewandelt werden");
		
		Object[] array = (Object[])data;
		return Arrays.asList(array);
	}

}
