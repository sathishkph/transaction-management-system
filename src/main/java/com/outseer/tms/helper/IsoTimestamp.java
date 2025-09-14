package com.outseer.tms.helper;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IsoTimestampValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsoTimestamp {
    String message() default "Invalid ISO 8601 timestamp format (expected: yyyy-MM-ddTHH:mm:ss or with offset)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

