package faang.school.postservice.dto.like;

import lombok.Builder;

@Builder
public record LikeDto(
         Long id,
         Long userId,
         Long postId,
         Long commentId
) {
}
