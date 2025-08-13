package faang.school.postservice.publisher.post.kafka;

import faang.school.postservice.dto.event.PostCreateEventDto;

public interface PostEventKafkaPublisher {
    void publish(PostCreateEventDto event);
}
