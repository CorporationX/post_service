package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeKafkaDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private KafkaTemplate<String, String> kafkaTemplate;
    private final LikeMapper likeMapper;
    private final ObjectMapper objectMapper;

    public void send(String topic, LikeEvent likeEvent) throws JsonProcessingException {

        LikeKafkaDto likeKafkaDto = likeMapper.toKafkaDto(likeEvent);
        String ms = objectMapper.writeValueAsString(likeKafkaDto);
        kafkaTemplate.send(topic, ms);
    }
}
