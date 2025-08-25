package faang.school.postservice.dto.comment;

public record CommentEvent(
        Long postId,
        Long postAuthorId,
        Long authorId,
        Long commentId,
        String content
) {
}
