package faang.school.postservice.service.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.TimePostId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class KafkaFeedProducer extends KafkaAbstractProducer {

    @Value("${spring.kafka.topics.post-topic}")
    private String postTopicName;

    public KafkaFeedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Async("kafkaThreadPool")
    public void sendFeed(KafkaKey key, List<Long> usersId, TimePostId timePostId) {
        for (Long id : usersId) {
            sendMessage(key, KafkaPostDto.builder()
                    .userId(id)
                    .timePostId(timePostId)
                    .build());
        }
    }

    @Override
    public void sendMessage(KafkaKey key, Object message) {
        var future = kafkaTemplate.send(postTopicName, key.name(), message);
        handleFuture(future);
    }
}
