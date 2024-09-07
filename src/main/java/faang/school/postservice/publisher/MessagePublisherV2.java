package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.event.LikeEventV2;

public interface MessagePublisherV2<T> {
    void publish(T likeEvent);
}
