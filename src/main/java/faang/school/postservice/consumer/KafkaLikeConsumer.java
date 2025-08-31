package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.service.PostLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {
    private final PostLikeService postLikeService;

    @KafkaListener(topics = "likes", groupId = "like-group")
    public void consume(LikeEvent event) {
        log.info("Получен лайк: postId={}, userId={}", event.getPostId(), event.getUserId());

        try {
            Long totalLikes = postLikeService.incrementLike(event.getPostId());
            log.info("Лайк добавлен: postId={}, totalLikes={}", event.getPostId(), totalLikes);
        } catch (Exception e) {
            log.error("Ошибка при обработке лайка", e);
            throw e;
        }
    }
}
