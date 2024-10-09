package faang.school.postservice.service.impl.like.publisher;

import faang.school.postservice.event.LikeEventDto;

public interface LikeEventPublisher {
    void publisher(LikeEventDto likeEventDto);
}
