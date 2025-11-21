package faang.school.postservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    @Value(value = "${comments-redis.max-comments-size}")
    private int maxCommentsSize;

    @KafkaListener(
            topics = "${comment-kafka.consumer.topic}",
            containerFactory = "kafkaListenerContainerFactory",
            groupId = "${comment-kafka.consumer.group-id}"
    )
    public void consume(@Payload String commentEventDto, Acknowledgment acknowledgment) {

        try {
            synchronized (this) {
                CommentEventDto commentEventObject = objectMapper.readValue(commentEventDto, CommentEventDto.class);
                System.out.println("commentEventObject = " + commentEventObject);

                Long postId = commentEventObject.getPostId();
                Optional<PostRedis> postById = redisPostRepository.findById(postId);
                if (postById.isPresent()) {
                    PostRedisDto postRedisDto = postById.get().getPost().get(0);

                    if (postRedisDto.getComments().size() == maxCommentsSize) {
                        postRedisDto.getComments().remove(0);
                    }

                    postRedisDto.getComments().add(
                            new CommentDto(commentEventObject.getId(),
                                    commentEventObject.getComment(),
                                    commentEventObject.getAuthorId(),
                                    4,
                                    commentEventObject.getPostId(),
                                    null,
                                    null
                            ));
                    postById.get().getPost().get(0).setComments(postRedisDto.getComments());
                    redisPostRepository.save(postById.get());
                    System.out.println("1 = " + 1);
                } else {
                    throw new Error("post not found");
                }
            }


        acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }
}
