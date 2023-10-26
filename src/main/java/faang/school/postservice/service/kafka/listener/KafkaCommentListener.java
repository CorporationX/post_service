package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaCommentListener extends KafkaAbstractListener {

    public KafkaCommentListener(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @KafkaListener(topics = "${spring.kafka.topics.comment-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> record) {
        log.info("Kafka Comment Listener consume message with key = {}", record.key());
        KafkaKey key = Enum.valueOf(KafkaKey.class, record.key());
        CommentDto commentDto = (CommentDto) record.value();
        if (key == KafkaKey.CREATE) {
            redisCacheService.addCommentToPost(commentDto);
        }
        if (key == KafkaKey.UPDATE) {
            redisCacheService.updateCommentOnPost(commentDto);
        }
        if (key == KafkaKey.DELETE) {
            redisCacheService.deleteCommentFromPost(commentDto);
        }
    }
}
