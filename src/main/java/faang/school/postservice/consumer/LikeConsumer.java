package faang.school.postservice.consumer;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeConsumer {
    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topics.like.name}")
    public void listen(LikeEvent event) {
        newsFeedService.addLikeToPost(event.postId());
    }
}
