package faang.school.postservice.messaging.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.PostPublishedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public class PostCreateKafkaConsumer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.data.kafka.topics.post-create.name}")
    public void listen(PostPublishedDto postPublishedDto, Acknowledgment acknowledgment) {

        acknowledgment.acknowledge();
    }

}
