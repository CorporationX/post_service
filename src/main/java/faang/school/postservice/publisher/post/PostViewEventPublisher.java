package faang.school.postservice.publisher.post;

import faang.school.postservice.dto.event.PostViewEventDto;

public interface PostViewEventPublisher {
    void publish(PostViewEventDto event);
}
