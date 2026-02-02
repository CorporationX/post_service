package faang.school.postservice.dto.post;

public record PostPublishedEventDto(
        Long postId,
        Long authorId,
        Long projectId
) {
}
