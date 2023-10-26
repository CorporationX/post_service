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

    @KafkaListener(topics = "${spring.kafka.topics.feed-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> record) {
        KafkaKey key = Enum.valueOf(KafkaKey.class, record.key());
        KafkaPostDto kafkaPostDto = (KafkaPostDto) record.value();
        if (key == KafkaKey.CREATE) {
            redisCacheService.addPostInFeed(kafkaPostDto);
            log.info("Post with id: {} add at feed to user: {}",
                    kafkaPostDto.getPost().getPostId(), kafkaPostDto.getUserId());
        }
        if (key == KafkaKey.DELETE) {
            redisCacheService.deletePostFromFeed(kafkaPostDto);
            log.info("Post with id: {} delete from feed to user: {}",
                    kafkaPostDto.getPost().getPostId(), kafkaPostDto.getUserId());
        }
    }
}
