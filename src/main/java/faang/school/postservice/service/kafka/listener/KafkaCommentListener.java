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

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.comment-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> message) {
        KafkaKey key = Enum.valueOf(KafkaKey.class, message.key());
        CommentDto commentDto = (CommentDto) message.value();
        log.info("KafkaCommentListener consume message with key={}, comment id ={}", key.name(), commentDto.getId());

        if (key == KafkaKey.CREATE) {
            redisCacheService.addCommentToPost(commentDto);
        } else if (key == KafkaKey.UPDATE) {
            redisCacheService.updateCommentInCache(commentDto);
        } else if (key == KafkaKey.DELETE) {
            redisCacheService.deleteCommentFromPost(commentDto);
        }
    }
}
