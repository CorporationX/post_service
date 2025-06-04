package faang.school.postservice.dto.like;

import lombok.Builder;

@Builder
public record LikePostResponseDto(
    Long id,
    Long userId,
    Long postId
) {
}
