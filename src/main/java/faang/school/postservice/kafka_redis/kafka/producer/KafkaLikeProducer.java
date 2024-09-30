package faang.school.postservice.kafka_redis.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.kafka_redis.kafka.model.LikeKafkaModel;
import faang.school.postservice.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;
    private final LikeMapper likeMapper;
    private final ObjectMapper objectMapper;

    public void send(String topic, LikeEvent likeEvent) throws JsonProcessingException {

        LikeKafkaModel likeModel = likeMapper.toKafkaDto(likeEvent);
        String ms = objectMapper.writeValueAsString(likeModel);
        kafkaTemplate.send(topic, ms);
    }
}
