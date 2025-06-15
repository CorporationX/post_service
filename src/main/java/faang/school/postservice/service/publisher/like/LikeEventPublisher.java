package faang.school.postservice.service.publisher.like;

import faang.school.postservice.dto.event.LikeEventDto;

public interface LikeEventPublisher {
    void publish(LikeEventDto event);
}
