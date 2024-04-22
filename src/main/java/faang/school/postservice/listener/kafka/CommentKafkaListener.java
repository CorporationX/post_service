package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.CommentPublishedEventDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.redis.RedisCommentCacheService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;


@RequiredArgsConstructor
public class CommentKafkaListener extends AbstractKafkaListener<CommentPublishedEventDto> {
    private final RedisCommentCacheService redisCommentCacheService;
    private final CommentService commentService;

    @KafkaListener(topics = "${kafka.topics.comment.name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, Object> message) {
        consume(message, CommentPublishedEventDto.class, (this::handleCommentEvent));
    }

    private void handleCommentEvent(CommentPublishedEventDto eventDto, KafkaKey kafkaKey) {

        if (kafkaKey == KafkaKey.SAVE) {
            redisCommentCacheService.addCommentToPost(commentService.getComment(eventDto.getCommentId()));
        }

        if (kafkaKey == KafkaKey.DELETE) {
            redisCommentCacheService.deleteCommentFromPost(eventDto.getPostId(), eventDto.getCommentId());
        }
    }
}

