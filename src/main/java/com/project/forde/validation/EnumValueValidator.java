package com.project.forde.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {
    private EnumValue annotation;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; //null 값 허용 여부
        }

        Enum<?>[] enumValues = this.annotation.enumClass().getEnumConstants();
        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                if (value.equals(enumValue.toString())
                        || this.annotation.ignoreCase() && value.equalsIgnoreCase(enumValue.toString())) {
                    return true;
                }
            }
        }

        return false;
    }
}