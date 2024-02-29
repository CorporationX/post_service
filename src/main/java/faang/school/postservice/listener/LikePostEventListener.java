package faang.school.postservice.listener;

import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikePostEventListener {
    private final PostHashService postHashService;

    @KafkaListener(topics = {
            "${spring.kafka.topics.like_post.name}",
            "${spring.kafka.topics.heat_feed.like_post}"
    })
    public void listen(LikePostEvent postViewEvent, Acknowledgment acknowledgment) {
        postHashService.updateLikePost(postViewEvent, acknowledgment);
    }
}
