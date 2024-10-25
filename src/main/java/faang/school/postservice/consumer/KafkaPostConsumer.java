package faang.school.postservice.consumer;

import faang.school.postservice.event.kafka.KafkaPostEvent;
import faang.school.postservice.producer.KafkaPostEventProducer;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer extends KafkaAbstractConsumer<KafkaPostEvent> {
    @Value("${spring.kafka.consumer.post-batch}")
    private Integer batchSize;
    private final FeedService feedService;
    private final KafkaPostEventProducer kafkaPostEventProducer;

    @KafkaListener(topics = "${spring.kafka.topic.posts.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(KafkaPostEvent kafkaPostEvent, Acknowledgment acknowledgment) {
        handle(kafkaPostEvent, acknowledgment, () -> {
            long postId = kafkaPostEvent.getPostId();
            List<Long> subscribersId = kafkaPostEvent.getSubscribersId();
            if (subscribersId.size() > batchSize) {
                for (int i = 0; i < subscribersId.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, subscribersId.size());
                    List<Long> batch = subscribersId.subList(i, end);
                    KafkaPostEvent kafkaBatchedPostEvent = KafkaPostEvent.builder()
                            .postId(postId)
                            .subscribersId(batch)
                            .build();
                    kafkaPostEventProducer.sendMessage(kafkaBatchedPostEvent);
                }
            } else {
                feedService.addPostIdToFollowers(postId, subscribersId);
            }
            subscribersId.forEach(subscriberId -> feedService.addPostIdToUserFeed(subscriberId, postId));
        });
    }
}
