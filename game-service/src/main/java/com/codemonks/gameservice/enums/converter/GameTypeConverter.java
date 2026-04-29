package com.codemonks.gameservice.enums.converter;

import com.codemonks.gameservice.enums.GameTypeEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class GameTypeConverter implements AttributeConverter<GameTypeEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GameTypeEnum attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public GameTypeEnum convertToEntityAttribute(Integer dbData) {
        return dbData != null ? GameTypeEnum.fromCode(dbData) : null;
    }
}
