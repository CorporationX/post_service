package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final CommentService commentService;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments}", groupId = "post-group")
    public void listenCommentEvent(KafkaCommentEvent event) {
        long postId = event.getPostId();
        RedisCommentDto dto = event.getCommentDto();

        log.info("Received comment event: Comment with ID {} will be added to Post with ID {}", dto.getId(), postId);
        commentService.addCommentToPost(postId, dto);
    }
}
