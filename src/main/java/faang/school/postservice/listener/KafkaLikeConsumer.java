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

            if (commentId == null) {
                log.info("Received like event: User with ID {} liked or unliked Post with ID {}", event.authorId(), postId);

                likeOrUnlikePost(postId, event.likeAction());
            } else {
                log.info("Received like event: User with ID {} liked or unliked comment in Post with ID {}", event.authorId(), postId);

                likeOrUnlikeComment(postId, commentId, event.likeAction());
            }
        } else {
            log.warn("Received empty Post or Comment like event");
        }
    }

    private void likeOrUnlikePost(long postId, LikeAction action) {
        switch (action) {
            case ADD -> {
                log.info("Like will be added to Post with ID: {}", postId);
                feedService.incrementOrDecrementPostLike(postId, action);
            }
            case REMOVE -> {
                log.info("Like will be removed from the Post with ID: {}", postId);
                feedService.incrementOrDecrementPostLike(postId, action);
            }
        }
    }

    private void likeOrUnlikeComment(long postId, long commentId, LikeAction action) {
        switch (action) {
            case ADD -> {
                log.info("Like will be added to comment in Post with ID: {}", postId);
                feedService.incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.ADD);
            }
            case REMOVE -> {
                log.info("Like will be removed from comment in Post with ID: {}", postId);
                feedService.incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.REMOVE);
            }
        }
    }
}
