package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventKafka;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${spring.kafka.topics.like}")
    private String topicLike;

    @Async("executor")
    public void sendMessage(LikeEventKafka likeEventKafka) {
        String msg = null;
        try {
            msg = new ObjectMapper().writeValueAsString(likeEventKafka);
        } catch (JsonProcessingException e) {
            log.error("Failed to make JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        kafkaTemplate.send(topicLike, msg);
    }
}
