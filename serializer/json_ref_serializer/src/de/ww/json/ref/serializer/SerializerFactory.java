package de.ww.json.ref.serializer;

public class SerializerFactory {

	private static SerializerImplementation serializerInstance = null;
	
	public static ISerializer getSerializerInstance() {
		if(serializerInstance == null) {
			serializerInstance = new SerializerImplementation();
		}
		return serializerInstance;
	}

	public static SerializerImplementation getSerializerTestInstance() {
		if(serializerInstance == null) {
			serializerInstance = new SerializerImplementation();
		}
		return serializerInstance;
	}
	
}
