package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewedEventConsumer {
    @Value("${spring.kafka.topics.post-viewed-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "my-group")
    public void listen(KafkaPostEventDto dto) {
        System.out.println("Received message: " + dto);
    }
}
