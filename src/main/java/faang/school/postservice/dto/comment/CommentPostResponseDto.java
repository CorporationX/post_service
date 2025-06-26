package faang.school.postservice.dto.comment;

import lombok.Builder;

@Builder
public record CommentPostResponseDto(
        Long id,
        String content,
        Long authorId,
        Long postId
) {
}