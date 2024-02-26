package faang.school.postservice.listener;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.service.hash.FeedHashService;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventListener {
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}")
    public void listen(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        postHashService.updateComment(commentEvent, acknowledgment);
    }
}
