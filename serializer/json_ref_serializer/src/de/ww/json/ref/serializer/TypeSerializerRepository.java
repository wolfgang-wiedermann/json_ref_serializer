package de.ww.json.ref.serializer;

import java.util.HashMap;

import de.ww.json.ref.serializer.exceptions.SerializerException;
import de.ww.json.ref.serializer.typeserializers.DateTypeSerializer;
import de.ww.json.ref.serializer.typeserializers.WrapperTypeSerializer;

/**
 * Ein Repository, das Serialisierer f�r verschiedene komplexere Typen wie
 * z. B. java.util.Date etc. bietet.
 * 
 * @author wiw39784
 *
 */
public class TypeSerializerRepository {
	private static TypeSerializerRepository instance = null;
	
	private HashMap<Class<?>, ITypeSerializer> serializers = new HashMap<Class<?>, ITypeSerializer>();
	
	/**
	 * L�dt das TypeSerializterRepository mit den Standard-Serialisieren
	 * 
	 * Weitere k�nnen zur Laufzeit via TypeSerializerRepository.getInstance().registerPlugin(...) 
	 * hinzugef�gt werden.
	 */
	private TypeSerializerRepository() {
		this.registerPlugin(new DateTypeSerializer());
		this.registerPlugin(new WrapperTypeSerializer());
	}
	
	public static TypeSerializerRepository getInstance() {
		if(instance == null) {
			instance = new TypeSerializerRepository();
		}
		return instance;
	}
	
	/**
	 * Registrieren eines weiteren Typ-Serialisierers
	 * @param plugin
	 */
	public void registerPlugin(ITypeSerializer plugin) {
		plugin.registerAsPlugin(this.serializers);
	}
	
	/**
	 * Pr�ft, ob f�r den angegebenen Typ ein serialisierer verf�gbar ist
	 * (Ber�cksichtigt derzeit keine Vererbungshierarchien!)
	 * @param type
	 * @return
	 */
	public boolean isSerializerAvailableFor(Class<?> type) {
		return serializers.containsKey(type);
	}
	
	/**
	 * Pr�ft ob f�r den angegebenen Typ ein serialisierer existiert und
	 * serialisiert diesen dann damit (sofern verf�gbar)
	 * 
	 * @param type
	 * @param object
	 * @return
	 * @throws SerializerException wenn keine geeignete Implementierung registriert ist. 
	 */
	public String serialize(Object object) throws SerializerException {
		
		if(!isSerializerAvailableFor(object.getClass()))
			throw new SerializerException("F�r den Typ "+object.getClass().getCanonicalName()+" ist kein serialisierer verf�gbar!");
				
		return serializers.get(object.getClass()).serialize(object);
	}
}
