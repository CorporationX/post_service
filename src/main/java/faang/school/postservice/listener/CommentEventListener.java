package faang.school.postservice.listener;

import faang.school.postservice.dto.event_broker.CommentUserEvent;
import faang.school.postservice.service.hash.PostHashService;
import faang.school.postservice.service.hash.UserHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventListener {
    private final PostHashService postHashService;
    private final UserHashService userHashService;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}")
    public void listen(CommentUserEvent commentUserEvent, Acknowledgment acknowledgment) {
        postHashService.updateComment(commentUserEvent, acknowledgment);
        userHashService.saveUser(commentUserEvent.getUserDto(), acknowledgment);
    }
}
