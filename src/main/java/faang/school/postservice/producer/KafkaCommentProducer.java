package faang.school.postservice.producer;

import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, PostDto postDto) {

        kafkaTemplate.send(topic, String.valueOf(postDto.getAuthorId()), postDto.getId());
    }
}
