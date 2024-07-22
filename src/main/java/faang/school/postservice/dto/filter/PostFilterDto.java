package faang.school.postservice.dto.filter;

public record PostFilterDto(
        Long userId,
        Long projectId,
        boolean isDeleted,
        boolean isPublished) {
}
