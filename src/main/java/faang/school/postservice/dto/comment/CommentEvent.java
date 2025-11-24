package faang.school.postservice.dto.comment;

public record CommentEvent(
        Long authorId,
        Long postId,
        Long commentId,
        String content
) {
}
