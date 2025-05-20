package faang.school.postservice.dto.like;

import lombok.Builder;

@Builder
public record LikeCommentResponseDto(
    Long id,
    Long userId,
    Long commentId
) {
}
