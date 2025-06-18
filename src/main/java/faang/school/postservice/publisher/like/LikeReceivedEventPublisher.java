package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.event.LikeReceivedEventDto;

public interface LikeReceivedEventPublisher {
    void publish(LikeReceivedEventDto event);
}
