package de.ww.json.ref.serializer;

import java.util.HashMap;

import de.ww.json.ref.serializer.exceptions.SerializerException;
import de.ww.json.ref.serializer.typeserializers.DateTypeSerializer;

/**
 * Ein Repository, das Serialisierer für verschiedene komplexere Typen wie
 * z. B. java.util.Date etc. bietet.
 * 
 * @author wiw39784
 *
 */
public class TypeSerializerRepository {
	private static TypeSerializerRepository instance = null;
	
	private HashMap<Class<?>, ITypeSerializer> serializers = new HashMap<Class<?>, ITypeSerializer>();
	
	private TypeSerializerRepository() {
		// TODO: hier die verschiedenen Implementierungen für die jeweiligen Typen hinzufügen
		serializers.put(java.util.Date.class, new DateTypeSerializer());
	}
	
	public static TypeSerializerRepository getInstance() {
		if(instance == null) {
			instance = new TypeSerializerRepository();
		}
		return instance;
	}
	
	/**
	 * Prüft, ob für den angegebenen Typ ein serialisierer verfügbar ist
	 * (Berücksichtigt derzeit keine Vererbungshierarchien!)
	 * @param type
	 * @return
	 */
	public boolean isSerializerAvailableFor(Class<?> type) {
		return serializers.containsKey(type);
	}
	
	/**
	 * Prüft ob für den angegebenen Typ ein serialisierer existiert und
	 * serialisiert diesen dann damit (sofern verfügbar)
	 * 
	 * @param type
	 * @param object
	 * @return
	 * @throws SerializerException wenn keine geeignete Implementierung registriert ist. 
	 */
	public String serialize(Object object) throws SerializerException {
		
		if(!isSerializerAvailableFor(object.getClass()))
			throw new SerializerException("Für den Typ "+object.getClass().getCanonicalName()+" ist kein serialisierer verfügbar!");
				
		return serializers.get(object.getClass()).serialize(object);
	}
}
