package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}", groupId = "post-consumer",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(KafkaPostEvent kafkaPostEvent, Acknowledgment acknowledgment) {
        log.info(String.format("#### -> Consumed message -> %s", kafkaPostEvent));
        feedService.addToFeed(kafkaPostEvent, acknowledgment);
    }

}
