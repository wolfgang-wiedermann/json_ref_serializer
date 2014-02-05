package de.ww.json.ref.serializer;

import java.util.HashMap;

import de.ww.json.ref.serializer.exceptions.SerializerException;

public interface ITypeSerializer {
	public String serialize(Object in) throws SerializerException;
	public void registerAsPlugin(HashMap<Class<?>, ITypeSerializer> repository);
}
