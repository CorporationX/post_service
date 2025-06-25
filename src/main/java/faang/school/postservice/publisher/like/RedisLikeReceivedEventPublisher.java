package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.event.LikeReceivedEventDto;

public interface RedisLikeReceivedEventPublisher {
    void publish(LikeReceivedEventDto event);
}
