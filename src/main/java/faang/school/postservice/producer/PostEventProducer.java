package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEventProducer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendPostEvent(PostEvent event) {
        try{
            List<Integer> randomListOfSubsribers = List.of(new Random().nextInt(100),
                    new Random().nextInt(100), new Random().nextInt(100),
                    new Random().nextInt(100), new Random().nextInt(100));
            event.setFollowersIds(randomListOfSubsribers);
            String data = objectMapper.writeValueAsString(event);
            String topic = "posts";
            log.info("Send new postEvent to Kafka: topic={}, data: {}", topic, data);
            kafkaTemplate.send(topic, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
