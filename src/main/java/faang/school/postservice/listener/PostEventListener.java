package faang.school.postservice.listener;

import faang.school.postservice.dto.event_broker.PostEvent;
import faang.school.postservice.service.hash.FeedHashService;
import faang.school.postservice.service.hash.PostHashService;
import faang.school.postservice.service.hash.UserHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final FeedHashService feedHashService;

    @KafkaListener(topics = {
            "${spring.kafka.topics.post.name}",
            "${spring.kafka.topics.heat_feed.post}"
    })
    public void listen(PostEvent postEvent, Acknowledgment acknowledgment) {
        feedHashService.updateFeed(postEvent, acknowledgment);
    }
}
