package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEventDto;

public interface EventPublisher<T> {
    void publisher(T event);
}
