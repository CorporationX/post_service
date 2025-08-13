package faang.school.postservice.config.redis.dto;

public record CommentEvent(
        Long commentId,
        Long postAuthorId,
        Long commentAuthorId,
        Long postId,
        String text
) {
}