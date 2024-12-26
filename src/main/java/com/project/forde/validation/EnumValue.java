package com.project.forde.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = { EnumValueValidator.class })
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    String message() default "Enum에 없는 값입니다.";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    Class<? extends Enum<?>> enumClass();
    boolean ignoreCase() default false;
}
