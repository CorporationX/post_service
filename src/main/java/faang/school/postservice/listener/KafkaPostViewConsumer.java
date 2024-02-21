package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostService postService;

    @KafkaListener(topics = "${spring.data.kafka.topics.post-view}", groupId = "${spring.kafka.client-id}")
    public void listenerPostViewEvent(PostViewEvent kafkaPostViewEvent, Acknowledgment acknowledgment) {
        log.info("Received post view event for post id: {}", kafkaPostViewEvent.getPostId());
        postService.saveViewToRedis(Objects.requireNonNull(kafkaPostViewEvent.getPostId()));
        acknowledgment.acknowledge();
    }
}
