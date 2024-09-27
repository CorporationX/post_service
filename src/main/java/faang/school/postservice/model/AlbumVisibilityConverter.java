package faang.school.postservice.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;
import java.util.stream.Stream;

@Converter(autoApply = true)
public class AlbumVisibilityConverter implements AttributeConverter<AlbumVisibility, String> {

    @Override
    public String convertToDatabaseColumn(AlbumVisibility attribute) {
        if (Objects.isNull(attribute)) {
            return null;
        }
        return attribute.getStatus();
    }

    @Override
    public AlbumVisibility convertToEntityAttribute(String dbData) {
        if (Objects.isNull(dbData)) {
            return null;
        }
        return Stream.of(AlbumVisibility.values())
                .filter(enumType -> enumType.getStatus().equals(dbData))
                .findFirst()
                .orElseThrow();
    }
}
