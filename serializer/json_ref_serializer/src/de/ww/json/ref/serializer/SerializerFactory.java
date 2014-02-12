package de.ww.json.ref.serializer;

/**
 * The SerializerFactory should be used to retrieve the one and only
 * serializer instance.
 * 
 * Internally implemented according to singleton pattern but intended
 * to hide the Methods of SerializerImplementation from the 
 * caller by offering it via ISerializer interface.
 * 
 * @author wiw39784
 */
public class SerializerFactory {

	private static SerializerImplementation serializerInstance = null;
	
	/**
	 * Usual method to retrieve a serializer instance
	 * @return
	 */
	public static ISerializer getSerializerInstance() {
		if(serializerInstance == null) {
			serializerInstance = new SerializerImplementation();
		}
		return serializerInstance;
	}

	/**
	 * Delivers the Serializer-Instance casted as SerializerImplementation
	 * for usage in Unit-Tests to directly address the methods
	 *  
	 * @return
	 */
	public static SerializerImplementation getSerializerTestInstance() {
		if(serializerInstance == null) {
			serializerInstance = new SerializerImplementation();
		}
		return serializerInstance;
	}
	
}
