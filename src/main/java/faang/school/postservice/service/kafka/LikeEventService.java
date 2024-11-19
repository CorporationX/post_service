package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.kafka.event.LikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.producer.KafkaLikeProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventService {
    private final KafkaLikeProducer producer;

    @Async("kafkaProducerExecutor")
    public void produce(LikeDto likeDto) {
        LikeEventDto eventDto = LikeEventDto.builder()
            .postId(likeDto.postId())
            .authorId(likeDto.userId())
            .build();
        producer.send(eventDto);
    }
}
