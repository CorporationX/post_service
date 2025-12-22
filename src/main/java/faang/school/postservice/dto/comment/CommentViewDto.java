package faang.school.postservice.dto.comment;

public record CommentViewDto(
        Long id,
        String content,
        Long authorId,
        Long postId,
        String largeImageFileKey,
        String smallImageFileKey
) {

}
