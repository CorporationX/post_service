package faang.school.postservice.dto.post;

import faang.school.postservice.exception.DataValidationException;

public enum PostOwnerType {
    AUTHOR, PROJECT;

    public static PostOwnerType fromString(String value) {
        try {
            return PostOwnerType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DataValidationException("Тип создателя не валиден: " + value);
        }
    }
}