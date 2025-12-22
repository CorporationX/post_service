package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record LikeEventDto(
        Long likeId,
        Long postId,
        Long authorId
) {
}
