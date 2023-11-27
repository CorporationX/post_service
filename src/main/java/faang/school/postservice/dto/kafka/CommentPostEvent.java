package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.Builder;

@Builder
public record CommentPostEvent(
        Long postId,
        RedisCommentDto commentDto,
        EventAction eventAction
) {
}
