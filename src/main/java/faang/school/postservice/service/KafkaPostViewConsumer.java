package faang.school.postservice.service;

import faang.school.postservice.event.PostViewEvent;

public interface KafkaPostViewConsumer {

    void consume(PostViewEvent postViewEvent);
}
