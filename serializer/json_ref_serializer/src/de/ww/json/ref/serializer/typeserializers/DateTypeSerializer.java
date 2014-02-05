package de.ww.json.ref.serializer.typeserializers;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.ww.json.ref.serializer.ITypeSerializer;
import de.ww.json.ref.serializer.exceptions.SerializerException;

public class DateTypeSerializer implements ITypeSerializer {

	public String serialize(Object in) throws SerializerException {
		if(!(in instanceof Date)) {
			throw new SerializerException("Das Objekt ist keine Instanz von java.util.Date. "
					+"Dieser Fehler sollte bereits durch das TypeSerializerRepository verhindert werden, "
					+" ggf. liegt hier ein Fehler vor.");
		}
		
		SimpleDateFormat format = new SimpleDateFormat();
		
		format.applyPattern("YYYY-MM-dd hh:mm:ss");
		
		return "new Date('"+format.format((Date)in)+"')";
	}

}
