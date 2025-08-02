package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentEventConsumer {

    private final FeedService feedService;
    @Value("${spring.kafka.topics.comment-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "my-group")
    public void listen(KafkaCommentEventDto dto) {
        System.out.println("Received message: " + dto);
        feedService.putUserIntoCache(dto.userId());
    }
}