package faang.school.postservice.producer;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, PostDto postDto) {
        LikeDto likeDto = new LikeDto();
        likeDto.setPostId(postDto.getId());
        kafkaTemplate.send(topic, String.valueOf(postDto.getId()),likeDto);
    }
}
