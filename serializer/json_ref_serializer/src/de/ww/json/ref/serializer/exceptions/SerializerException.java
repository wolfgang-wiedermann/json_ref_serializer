package de.ww.json.ref.serializer.exceptions;

public class SerializerException extends Exception {

	private static final long serialVersionUID = 1L;

	public SerializerException () { super(); }
	public SerializerException (Throwable nested) { super(nested); }
	public SerializerException (String message) { super(message); }
	
}
