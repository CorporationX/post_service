package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaFeedListener extends KafkaAbstractListener {

    public KafkaFeedListener(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.post-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> message) {
        KafkaKey key = Enum.valueOf(KafkaKey.class, message.key());
        KafkaPostDto kafkaDto = (KafkaPostDto) message.value();
        log.info("KafkaFeedListener consume message with key={}, post id={}", key.name(), kafkaDto.getTimePostId().getPostId());

        if (key == KafkaKey.CREATE) {
            redisCacheService.saveFeed(kafkaDto);
        } else if (key == KafkaKey.DELETE) {
            redisCacheService.deletePostFromFeed(kafkaDto);
        }
    }
}
