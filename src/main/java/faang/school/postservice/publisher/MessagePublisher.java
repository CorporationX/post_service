package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;

public interface MessagePublisher {
    void publish(LikeEvent likeEvent);
}