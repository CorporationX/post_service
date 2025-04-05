package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostCommentEventProducer extends KafkaProducerService {

    public PostCommentEventProducer(KafkaTemplate<String, PostCommentEvent> kafkaTemplate,
                                    ObjectMapper objectMapper,
                                    @Value("${spring.kafka.topic.post-comments-topic}") String topic) {
        super(kafkaTemplate, objectMapper, topic);
    }

    @Async("asyncTaskExecutor")
    public void produceCommentPostEventAsync(Comment comment) {
        produceCommentPostEvent(comment);
    }

    public void produceCommentPostEvent(Comment comment) {

        long postId = comment.getPost().getId();
        PostCommentEvent postCommentEvent = PostCommentEvent.builder()
                .postId(postId)
                .commentId(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();

        super.sendMessage(postCommentEvent);
        log.info("Sending PostCommentEvent to message broker. Post : {}", postId);
    }
}
