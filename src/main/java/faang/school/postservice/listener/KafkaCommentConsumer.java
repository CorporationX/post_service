package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final CommentService commentService;
    @KafkaListener(topics = "${spring.data.kafka.topics.comment}", groupId = "comment-group")
    public void listenerCommentEvent(KafkaCommentEvent kafkaCommentEvent, Acknowledgment acknowledgment) {
        commentService.changeListComments(kafkaCommentEvent.getComment(), kafkaCommentEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
