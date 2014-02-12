package de.ww.json.ref.serializer;

import de.ww.json.ref.serializer.exceptions.SerializerException;

/**
 * The ISerializer interface is intended to be used as an
 * interface showing only the main functionality of the
 * SerializerImplementation.
 * 
 * @author wiw39784
 */
public interface ISerializer {
	public String serialize(Object data) throws SerializerException;
}
