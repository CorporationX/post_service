package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentEventConsumer {

    private final FeedService feedService;
    @Value("${spring.kafka.topics.comment-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "${spring.kafka.group-id}")
    public void listen(KafkaCommentEventDto dto, Acknowledgment acknowledgment) {
        if (feedService.updatePost(dto.postId(), topic) && feedService.putCommentInCache(dto)) {
            acknowledgment.acknowledge();
        }
    }
}