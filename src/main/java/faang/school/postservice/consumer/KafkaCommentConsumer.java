package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}")
    public void listen(CommentEventKafka commentEventKafka, Acknowledgment acknowledgment) {
        postHashService.addComment(commentEventKafka);
        acknowledgment.acknowledge();
    }
}
