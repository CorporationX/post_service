package faang.school.postservice.listener;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.service.FeedHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final FeedHashService feedHashService;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}")
    public void listen(PostEvent postEvent, Acknowledgment acknowledgment) {
        feedHashService.updateFeed(postEvent, acknowledgment);
    }
}
