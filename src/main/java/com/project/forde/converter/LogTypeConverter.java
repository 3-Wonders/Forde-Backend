package com.project.forde.converter;

import com.project.forde.type.LogTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LogTypeConverter implements AttributeConverter<LogTypeEnum, String> {
    @Override
    public String convertToDatabaseColumn(LogTypeEnum attribute) {
        if (attribute == null) {
            return null;
        }

        return attribute.getLogType().toUpperCase();
    }

    @Override
    public LogTypeEnum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        return LogTypeEnum.valueOf(dbData.toUpperCase());
    }
}
