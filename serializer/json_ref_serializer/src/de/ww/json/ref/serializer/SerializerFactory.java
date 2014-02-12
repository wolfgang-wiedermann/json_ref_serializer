package de.ww.json.ref.serializer;

public class SerializerFactory {

	private static Serializer serializerInstance = null;
	
	public static ISerializer getSerializerInstance() {
		if(serializerInstance == null) {
			serializerInstance = new Serializer();
		}
		return serializerInstance;
	}

	public static Serializer getSerializerTestInstance() {
		if(serializerInstance == null) {
			serializerInstance = new Serializer();
		}
		return serializerInstance;
	}
	
}
