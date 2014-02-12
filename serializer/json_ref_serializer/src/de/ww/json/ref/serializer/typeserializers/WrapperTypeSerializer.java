package de.ww.json.ref.serializer.typeserializers;

import java.util.HashMap;

import de.ww.json.ref.serializer.ITypeSerializer;
import de.ww.json.ref.serializer.SerializerImplementation;
import de.ww.json.ref.serializer.exceptions.SerializerException;

public class WrapperTypeSerializer implements ITypeSerializer {

	public String serialize(Object in) throws SerializerException {
		if(in instanceof String)
			return SerializerImplementation.STRING_DELIMITER+in.toString()+SerializerImplementation.STRING_DELIMITER;
		else
			return in.toString();
	}

	public void registerAsPlugin(HashMap<Class<?>, ITypeSerializer> repository) {
		repository.put(String.class, this);
		repository.put(Boolean.class, this);
		repository.put(Integer.class, this);
		repository.put(Long.class, this);
		repository.put(Double.class, this);
		repository.put(Float.class, this);
		// TODO: evtl. ergänzen
	}

}
