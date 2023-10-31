package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final LikeService likeService;

    @KafkaListener(topics = "${spring.kafka.topics.likes-topic}", groupId = "${spring.kafka.client-id}")
    public void listenerLikeEvent(KafkaLikeEvent kafkaLikeEvent, Acknowledgment acknowledgment) {
        likeService.incrementLike(kafkaLikeEvent.getPostId());
        acknowledgment.acknowledge();
    }
}
