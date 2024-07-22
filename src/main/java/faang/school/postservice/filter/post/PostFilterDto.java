package faang.school.postservice.filter.post;

public record PostFilterDto(
        Long userId,
        Long projectId,
        boolean isDeleted,
        boolean isPublished) {
}
