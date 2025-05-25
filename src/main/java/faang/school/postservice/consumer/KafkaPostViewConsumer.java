package faang.school.postservice.consumer;

import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.service.PostViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostViewService postViewService;

    @KafkaListener(
            topics = "${app.kafka.producer.topics.post-view}",
            groupId = "${app.kafka.consumers.post-view}"
    )
    public void listen(PostViewEvent event) {
        postViewService.handlePostViewEvent(event);
    }
}
