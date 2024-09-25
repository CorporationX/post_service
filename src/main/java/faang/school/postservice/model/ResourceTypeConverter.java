package faang.school.postservice.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class ResourceTypeConverter implements AttributeConverter<ResourceType, String> {

    @Override
    public String convertToDatabaseColumn(ResourceType type) {
        if (Objects.isNull(type)) {
            return null;
        }
        return type.getType();
    }

    @Override
    public ResourceType convertToEntityAttribute(String type) {
        if (Objects.isNull(type)) {
            return null;
        }
        return Stream.of(ResourceType.values())
                .filter(enumType -> enumType.getType().equals(type))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
