package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;

public interface LikeEventPublisher {
    void publishLikeEvent(LikeEvent event);
}
