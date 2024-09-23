package faang.school.postservice.messaging.kafka.listener.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentsEvent;
import faang.school.postservice.service.redis.RedisPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final ObjectMapper objectMapper;
    private final RedisPostService redisPostService;

    @KafkaListener(topics = "${spring.kafka.topic.comments}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(String message, Acknowledgment acknowledgment) {
        try {
            CommentsEvent commentsEvent = objectMapper.readValue(message.getBytes(), CommentsEvent.class);
            CommentDto commentDto = CommentDto.builder()
                    .id(commentsEvent.getCommentId())
                    .content(commentsEvent.getContent())
                    .authorId(commentsEvent.getAuthorId())
                    .likesId(new ArrayList<>())
                    .postId(commentsEvent.getPostId())
                    .build();

//            redisPostService.updateCommentDtoInPost(commentDto);

            acknowledgment.acknowledge();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}