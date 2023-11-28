package faang.school.postservice.listener;

import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "${spring.data.kafka.topics.likes.name}", groupId = "post-group")
    public void listenLikeEvent(LikeEvent event) {
        if (event != null) {
            Long postId = Objects.requireNonNull(event.postId(), "Post ID cannot be null for liking or unliking a post");
            Long commentId = event.commentId();
            LikeAction action = event.likeAction();

            if (commentId == null) {
                log.info("Received like event: User with ID {} liked or unliked Post with ID {}", event.authorId(), postId);

                feedService.incrementOrDecrementPostLike(postId, action);
            } else {
                log.info("Received like event: User with ID {} liked or unliked comment in Post with ID {}", event.authorId(), postId);

                feedService.incrementOrDecrementPostCommentLike(postId, commentId, action);
            }
        } else {
            log.warn("Received empty Post or Comment like event");
        }
    }
}
