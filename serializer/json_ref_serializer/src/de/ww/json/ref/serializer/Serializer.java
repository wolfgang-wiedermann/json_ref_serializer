package de.ww.json.ref.serializer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.ww.json.ref.serializer.annotations.JustReference;
import de.ww.json.ref.serializer.annotations.RestPath;

/**
 * JSON-Serialisierer, der es ermöglicht Beziehungen zwischen Objekten
 * wahlweise direkt in den JSON-String zu serialisieren (standard anderer Serialisierer)
 * oder als Hyperlink-Referenz (zum Nachladen) anzugeben.
 * 
 * siehe z. B. http://en.wikipedia.org/wiki/HATEOAS
 * 
 * TODO: Exception-Handling
 * TODO: JustReference bei Listen ...
 * 
 * @author wiw39784
 *
 */
public class Serializer {

	private static String OBJECT_OPEN = "{"
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
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static String serialize(Object data) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Class<?> type = data.getClass();
		Method methods[] = type.getMethods();
		StringBuffer buffer = new StringBuffer();
		buffer.append(OBJECT_OPEN);
		for(Method m : methods) {
			if(isScalarGetter(m)) {				
				writeScalar(buffer, data, m);
			} else if(isListGetter(m)) {
				writeList(buffer, data, m);
			}
		}
		buffer.append(OBJECT_CLOSE);
		return buffer.toString();
	}
	
	/**
	 * 
	 * @param buffer
	 * @param data
	 * @param m
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private static void writeList(StringBuffer buffer, Object data, Method m) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String fieldName = getNormalizedGetterString(m);
		Object list = m.invoke(data);		
		buffer.append(STRING_DELIMITER);
		buffer.append(fieldName);
		buffer.append(STRING_DELIMITER);
		buffer.append(NAME_DELIMITER);
		buffer.append(ARRAY_OPEN);
		if(list != null) {
			Iterable<?> castedList = (Iterable<?>)list;
			for(Object o : castedList) {
				if(o.getClass().isPrimitive() || o.getClass().equals(String.class)) {
					buffer.append(STRING_DELIMITER+o+STRING_DELIMITER);
				} else {
					buffer.append(serialize(o));
				}
				buffer.append(FIELD_DELIMITER);
			}
		} 
		buffer.append(ARRAY_CLOSE);
		buffer.append(FIELD_DELIMITER);
		
	}

	/**
	 * Einen Eintrag für ein Skalar-Feld schreiben
	 * @param buffer
	 * @param data
	 * @param m
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private static void writeScalar(StringBuffer buffer, Object data, Method m) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
	 * Ermittelt den Wert eines Feldes
	 * @param data
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	private static Object getValue(Object data, Method m) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object value = null;		
		if(m.getReturnType().isPrimitive() 
				|| m.getReturnType().equals(String.class) ) {
			value = m.invoke(data);
			value = STRING_DELIMITER+value+STRING_DELIMITER;
		} else {
			Object result = m.invoke(data);
			if(m.isAnnotationPresent(JustReference.class)) {
				if(result != null) {
					value = writeReference(result);
				} else {
					value = STRING_DELIMITER+"null"+STRING_DELIMITER;
				}
			} else {				
				if(result != null) {
					value = serialize(result);
				} else {
					value = STRING_DELIMITER+"null"+STRING_DELIMITER;
				}
			}
		}
		return value;
	}

	/**
	 * Schreibt ein Referenz-Objekt anstelle des eigentlichen JSON-Objekts
	 * 
	 * TODO: Zerlegen in Teilmethoden 
	 * 
	 * @param result
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	private static Object writeReference(Object result) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		
		if(!result.getClass().isAnnotationPresent(RestPath.class))
			throw new IllegalStateException("Referenzen sind nur für Entities mit RestPath-Annotation zulässig");
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(OBJECT_OPEN);
		buffer.append("'url':'");
		// TODO: in Zukunft noch alle möglichen enthaltenen {}-Parameter ersetzen!
		String urlTemplate = result.getClass().getAnnotation(RestPath.class).value();
		String url = urlTemplate.replaceAll("\\{id\\}", result.getClass().getMethod("getId").invoke(result).toString());
		// ----
		buffer.append(url);
		buffer.append("'");
		buffer.append(FIELD_DELIMITER);
		// TODO: hier noch eine richtige Lade-Funktion generieren...
		buffer.append("'get':function(successHandler, errorHandler) { alert(this.url); }");
		buffer.append(OBJECT_CLOSE);
		return buffer.toString();
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

}
