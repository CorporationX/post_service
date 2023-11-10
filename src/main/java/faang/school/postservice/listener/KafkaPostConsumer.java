package faang.school.postservice.listener;

import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.post.name}", groupId = "post-group")
    public void listenPostEvent(PostEvent event) {
        if (event != null) {
            PostPair postPair = event.postPair();
            long postId = postPair.postId();

            List<Long> followeesIds = event.followersIds();
            log.info("Received Post event with Post ID: {}, and amount of followers: {}", postId, followeesIds.size());

            EventAction action = event.eventAction();
            switch (action) {
                case CREATE -> {
                    log.info("Received Create Event action for Post with ID: {}", postId);
                    followeesIds.forEach(userId -> feedService.saveSinglePostToFeed(userId, postPair));
                }
                case UPDATE -> {
                    log.info("Received Update Event action for Post with ID: {}", postId);
                    feedService.updateSinglePostInRedis(postId);
                }
                case DELETE -> {
                    log.info("Received Delete Event action for Post with ID: {}", postId);
                    feedService.deleteSinglePostInFeed(followeesIds, postPair.postId());
                }
            }
        } else {
            log.warn("Received empty Post publishing event");
        }
    }
}
