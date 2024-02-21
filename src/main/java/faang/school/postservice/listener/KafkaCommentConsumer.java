package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.NewCommentEvent;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {
    private final CommentService commentService;
    @KafkaListener(topics = "${spring.kafka.topics.comments-topic}", groupId = "${spring.kafka.client-id}")
    public void listenerCommentEvent(NewCommentEvent newCommentEvent, Acknowledgment acknowledgment) {
        log.info("Received to change comments for post: {}", newCommentEvent.getPostId());
        commentService.changeListComments(Objects.requireNonNull(newCommentEvent.getComment()), Objects.requireNonNull(newCommentEvent.getPostId()));
        acknowledgment.acknowledge();
    }
}
