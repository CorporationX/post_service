package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostEventConsumer {

    private final FeedService feedService;

    @Value("${spring.kafka.topics.post-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "my-group")
    public void listen(KafkaPostEventDto dto) {
        System.out.println("Received message: " + dto);
        feedService.addCreatedPostToSubscribers(dto);
    }
}