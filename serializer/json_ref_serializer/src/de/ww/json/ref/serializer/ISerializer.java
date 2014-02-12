package de.ww.json.ref.serializer;

import de.ww.json.ref.serializer.exceptions.SerializerException;

public interface ISerializer {
	public String serialize(Object data) throws SerializerException;
}
