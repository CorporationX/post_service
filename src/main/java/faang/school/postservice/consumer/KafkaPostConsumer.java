package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.service.hash.FeedHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final FeedHashService feedHashService;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}")
    public void listen(PostEventKafka postEventKafka, Acknowledgment acknowledgment) {
        feedHashService.updateFeed(postEventKafka);
        acknowledgment.acknowledge();
    }

}
