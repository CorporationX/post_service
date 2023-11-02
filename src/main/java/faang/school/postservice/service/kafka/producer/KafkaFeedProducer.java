package faang.school.postservice.service.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.TimedPostId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaFeedProducer extends KafkaAbstractProducer {

    @Value("${spring.kafka.topics.feed-topic.name}")
    private String feedTopicName;

    public KafkaFeedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void sendFeed(KafkaKey key, List<Long> followers, TimedPostId timedPostId) {
        for (Long follower : followers) {
            KafkaPostDto kafkaPostDto = KafkaPostDto.builder()
                    .userId(follower)
                    .post(timedPostId)
                    .build();
            sendMessage(key, kafkaPostDto);
        }
    }

    @Override
    @Async("kafkaThreadPool")
    public void sendMessage(KafkaKey key, Object kafkaPostDto) {
        var future = kafkaTemplate.send(feedTopicName, key.name(), kafkaPostDto);
        handleFuture(future);
    }
}
