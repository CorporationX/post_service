package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.ViewEventKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${spring.kafka.topics.view}")
    private String topicView;

    @Async("executor")
    public void sendMessage(ViewEventKafka viewEventKafka) {
        String msg = null;
        try {
            msg = new ObjectMapper().writeValueAsString(viewEventKafka);
        } catch (JsonProcessingException e) {
            log.error("Failed to make JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        kafkaTemplate.send(topicView, msg);
    }
}
