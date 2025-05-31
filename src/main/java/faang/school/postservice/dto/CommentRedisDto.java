package faang.school.postservice.dto;

public record CommentRedisDto(
        String content,
        long authorId,
        long amountLikes
) {
}
