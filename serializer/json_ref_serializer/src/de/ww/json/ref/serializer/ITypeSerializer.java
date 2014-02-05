package de.ww.json.ref.serializer;

import de.ww.json.ref.serializer.exceptions.SerializerException;

public interface ITypeSerializer {
	String serialize(Object in) throws SerializerException;
}
