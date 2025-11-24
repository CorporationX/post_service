package faang.school.postservice.dto.comment;

public record CommentEvent(
        Long authorId,
        Long PostId,
        Long commentId,
        String content
) {
}
