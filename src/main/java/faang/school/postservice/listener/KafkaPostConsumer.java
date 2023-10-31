package faang.school.postservice.listener;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final FeedService feedService;

    @Async("postsThreadPool")
    @KafkaListener(topics = "${spring.data.kafka.topics.post}", groupId = "post-group")
    public void listenPostEvent(KafkaPostEvent event) {
        PostPair postPair = event.getPostPair();

        log.info("Received event with Post ID: {}, and amount of followers: {}", postPair.postId(), event.getFollowersIds().size());
        event.getFollowersIds()
                .forEach(userId -> feedService.saveSinglePostToFeed(userId, postPair));
    }
}
