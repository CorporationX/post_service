package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.ViewEventKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaProducer<T> {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Async("executor")
    public void sendMessage(T eventKafka, String topic) {
        String msg = null;
        try {
            msg = new ObjectMapper().writeValueAsString(eventKafka);
        } catch (JsonProcessingException e) {
            log.error("Failed to make JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        kafkaTemplate.send(topic, msg);
    }
}
