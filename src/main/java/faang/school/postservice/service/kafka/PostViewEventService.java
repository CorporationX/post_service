package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.kafka.event.PostViewEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.producer.KafkaPostViewProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewEventService {
    private final KafkaPostViewProducer producer;

    @Async("kafkaProducerExecutor")
    public void produce(Long userId, PostDto postDto) {
        PostViewEventDto eventDto = PostViewEventDto.builder()
            .postId(postDto.id())
            .userId(userId)
            .build();
        producer.send(eventDto);
    }
}
