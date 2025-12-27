package faang.school.postservice.listener;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.service.cash.HashCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final HashCommentService hashCommentService;

    @KafkaListener(topics = "${spring.data.kafka.topic.comment.name}",
            groupId = "${spring.data.kafka.topic.comment.group-id}",
            containerFactory = "manualAckKafkaListenerContainerFactory")
    public void listen(CommentEvent event, Acknowledgment ack) {
        try {
            hashCommentService.addComment(event);
        } catch (RuntimeException e) {
            log.error("Error while adding comment to hash", e);
        }
        log.info("Comment with id {} added to post with id {}", event.commentId(), event.postId());
        ack.acknowledge();
    }
}
