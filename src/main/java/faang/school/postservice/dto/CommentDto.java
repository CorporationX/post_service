package faang.school.postservice.dto;

public record CommentDto(
        long id,
        String content,
        long authorId,
        long amountLikes,
        long postId
) {
}
