package com.example.springsecurity.domain.user.converter;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class AuthorityListConverter implements AttributeConverter<List<AuthorityEnum>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<AuthorityEnum> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
            .map(Enum::name)
            .collect(Collectors.joining(SEPARATOR));
    }

    @Override
    public List<AuthorityEnum> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
            .map(AuthorityEnum::valueOf)
            .collect(Collectors.toList());
    }
}

