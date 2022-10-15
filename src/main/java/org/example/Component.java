package org.example;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Component {
}
