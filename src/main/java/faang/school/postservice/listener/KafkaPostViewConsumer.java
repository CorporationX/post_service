package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.post-views.name}", groupId = "post-group")
    public void listenPostViewEvent(PostViewEvent event) {
        if (event != null) {
            Long postId = event.postId();
            log.info("Received Post view event. Attempting to increment view of Post with ID: {}", postId);

            feedService.incrementPostView(postId);
        } else {
            log.warn("Received empty Post view event");
        }
    }
}

