package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;

public interface MessagePublisher {
    void publish(PostViewEvent postViewEvent);
}
