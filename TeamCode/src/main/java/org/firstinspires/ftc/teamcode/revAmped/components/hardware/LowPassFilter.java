package org.firstinspires.ftc.teamcode.revAmped.components.hardware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LowPassFilter {
    int order() default 1;
    double alpha();
    boolean scale() default false;
}