package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.service.hash.PostHashService;
import faang.school.postservice.service.hash.UserHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {
    private final PostHashService postHashService;
    private final UserHashService userHashService;

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}")
    public void listen(CommentEventKafka commentEventKafka, Acknowledgment acknowledgment) {
        postHashService.addComment(commentEventKafka);
        userHashService.saveAuthor(commentEventKafka.getUserDto());
        acknowledgment.acknowledge();
    }
}
