package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class KafkaPostProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${spring.kafka.topics.post}")
    private String topicPost;

    @Async("executor")
    public void sendMessage(Long authorId, List<Long> followerIds) {
        Map<String, Object> message = new HashMap<>();
        message.put("authorId", authorId);
        message.put("followerIds", followerIds);

        ObjectMapper objectMapper = new ObjectMapper();
        String msg = null;
        try {
            msg = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error("Failed to make JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        kafkaTemplate.send(topicPost, msg);
    }
}
