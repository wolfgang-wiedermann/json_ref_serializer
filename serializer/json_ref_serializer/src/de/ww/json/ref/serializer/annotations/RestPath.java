package de.ww.json.ref.serializer.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Pfadangabe f�r den Lookup einer (referenzierten) Entit�t via REST
 * 
 * @author wiw39784
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RestPath {
	String value();
}
