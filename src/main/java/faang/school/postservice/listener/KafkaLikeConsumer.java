package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.LikePostEvent;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final LikeService likeService;

    @KafkaListener(topics = "${spring.kafka.topics.likes-topic}", groupId = "${spring.kafka.client-id}")
    public void listenerLikeEvent(LikePostEvent kafkaLikeEvent, Acknowledgment acknowledgment) {
        log.info("Received like event by author id: {}", kafkaLikeEvent.getAuthorId());
        likeService.incrementLike(Objects.requireNonNull(kafkaLikeEvent.getPostId()));
        acknowledgment.acknowledge();
    }
}
