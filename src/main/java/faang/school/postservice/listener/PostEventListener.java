package faang.school.postservice.listener;

import faang.school.postservice.dto.post.PostEvent;
import faang.school.postservice.service.hash.FeedHashService;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final FeedHashService feedHashService;
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}")
    public void listen(PostEvent postEvent, Acknowledgment acknowledgment) {
        feedHashService.updateFeed(postEvent, acknowledgment);
        postHashService.savePost(postEvent, acknowledgment);
    }
}
