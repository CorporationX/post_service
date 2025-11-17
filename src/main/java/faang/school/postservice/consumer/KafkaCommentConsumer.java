package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "comment-kafka.enabled", havingValue = "true")
@Slf4j
public class KafkaCommentConsumer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RedisPostRepository redisPostRepository;
    private int maxSize = 3;

    @KafkaListener(
            topics = "${comment-kafka.consumer.topic}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${comment-kafka.consumer.group-id}"
    )
    public void consume(@Payload String commentEventDto, Acknowledgment acknowledgment) {

        try {
            CommentEventDto commentEventObject = objectMapper.readValue(commentEventDto, CommentEventDto.class);
            System.out.println("commentEventObject = " + commentEventObject);

            Long postId = commentEventObject.getPostId();
            Optional<PostRedis> postById = redisPostRepository.findById(postId);
            if (postById.isPresent()) {
//                postById.get().getPost().
            }
        acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
