package faang.school.postservice.dto.kafka;

import lombok.Builder;

@Builder
public record LikeEvent(
        Long postId,
        Long commentId,
        Long authorId,
        LikeAction likeAction
) {
}
