package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final LikeService likeService;

    @Async("likeThreadPool")
    @KafkaListener(topics = "${spring.data.kafka.topics.likes}", groupId = "post-group")
    public void listenLikeEvent(KafkaLikeEvent event) {
        long postId = event.getPostId();

        log.info("Received like event: User with ID {} liked Post with ID {}", event.getAuthorId(), postId);
        likeService.incrementRedisPostLike(postId);
    }
}
