package faang.school.postservice.dto.comment;

public record CommentResponse(Long id,
                               String content,
                               Long authorId,
                               Long postId,
                               Integer likeCount) {
}
