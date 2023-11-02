package faang.school.postservice.service.kafka.listener;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.service.RedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaLikeListener extends KafkaAbstractListener {

    public KafkaLikeListener(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @KafkaListener(topics = "${spring.kafka.topics.like-topic.name}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> record) {
        log.info("Kafka Like Listener consume message with key = {}", record.key());
        KafkaKey key = Enum.valueOf(KafkaKey.class, record.key());
        LikeDto likeDto = (LikeDto) record.value();
        if (key == KafkaKey.CREATE) {
            addLike(likeDto);
        }else if (key == KafkaKey.DELETE) {
            deleteLike(likeDto);
        } else {
            log.info("Unexpected key in Kafka Like Listener, no actions completed  = {}", record.key());
        }
    }

    private void addLike(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            redisCacheService.addLikeOnPost(likeDto);
        } else {
            redisCacheService.addLikeToComment(likeDto.getPostId(), likeDto.getCommentId());
        }
    }

    private void deleteLike(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            redisCacheService.deleteLikeFromPost(likeDto.getPostId());
        } else {
            redisCacheService.deleteLikeFromComment(likeDto.getPostId(), likeDto.getCommentId());
        }
    }
}
