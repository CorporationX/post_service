package faang.school.postservice.dto;

public record ExceptionDto(
        int code,
        String description,
        String error) {
}
