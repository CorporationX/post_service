package faang.school.postservice.consumer;

import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.ProcessingEventException;
import faang.school.postservice.service.PostViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostViewService postViewService;

    @KafkaListener(
            topics = "${app.kafka.producer.topics.post-view}",
            groupId = "${app.kafka.consumers.post-view}"
    )
    public void listen(
            PostViewEvent event,
            Acknowledgment ack
    ) {
        try {
            postViewService.handlePostViewEvent(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Technical error processing event");
            throw new ProcessingEventException("Technical error processing event", e);
        }
    }
}
