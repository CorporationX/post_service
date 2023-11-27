package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.CommentPostEvent;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.comments.name}", groupId = "post-group")
    public void listenCommentEvent(CommentPostEvent event) {
        if (event != null) {
            long postId = event.postId();
            RedisCommentDto dto = event.commentDto();
            log.info("Received comment event with CommentID {} and Post ID: {}", dto.getId(), postId);

            EventAction action = event.eventAction();
            switch (action) {
                case CREATE -> {
                    log.info("Received Create Event action. Comment will be added to Post with ID: {}", postId);
                    feedService.addCommentToPost(postId, dto);
                }
                case UPDATE -> {
                    log.info("Received Update Event action. Comment will updated in Post with ID: {}", postId);
                    feedService.updateCommentInPost(postId, dto);
                }
                case DELETE -> {
                    log.info("Received Delete Event action. Comment will removed from the Post with ID: {}", postId);
                    feedService.deleteCommentFromPost(postId, dto.getId());
                }
            }
        } else {
            log.warn("Received empty comment Post event");
        }
    }
}
