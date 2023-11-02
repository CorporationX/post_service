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

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.like-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(ConsumerRecord<String, Object> message) {
        KafkaKey key = Enum.valueOf(KafkaKey.class, message.key());
        LikeDto likeDto = (LikeDto) message.value();
        log.info("KafkaLikeListener consume message with key={}, like id ={}", key.name(), likeDto.getId());

        if (key == KafkaKey.CREATE) {
            addLike(likeDto);
        } else if (key == KafkaKey.DELETE) {
            deleteLike(likeDto);
        }
    }

    private void addLike(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            redisCacheService.addLikeToPost(likeDto);
        } else {
            redisCacheService.addLikeToComment(likeDto);
        }
    }

    private void deleteLike(LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            redisCacheService.deleteLikeFromPost(likeDto);
        } else {
            redisCacheService.deleteLikeFromComment(likeDto);
        }
    }
}
