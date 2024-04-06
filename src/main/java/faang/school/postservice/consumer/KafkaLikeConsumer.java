package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.like.name}")
    public void listen(LikeEventKafka likeEventKafka, Acknowledgment acknowledgment) {
        postHashService.addLikeToPost(likeEventKafka);
        acknowledgment.acknowledge();
    }
}
