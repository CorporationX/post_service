package faang.school.postservice.listener;

import faang.school.postservice.dto.event_broker.PostViewEvent;
import faang.school.postservice.service.hash.PostHashService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventListener {
    private final PostHashService postHashService;

    @KafkaListener(topics = "${spring.kafka.topics.post_view.name}")
    public void listen(PostViewEvent postViewEvent, Acknowledgment acknowledgment) {
        postHashService.updatePostViews(postViewEvent, acknowledgment);
    }
}
