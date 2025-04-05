package faang.school.postservice.dto.project;

public record ProjectResponseDto(
        Long id,
        Long ownerId,
        String name,
        String status,
        String description
) {
}
