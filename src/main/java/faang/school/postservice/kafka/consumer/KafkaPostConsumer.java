package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.FeedUpdateEvent;
import faang.school.postservice.kafka.event.NewPostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.service.feed.UserFeedZSetService;
import faang.school.postservice.service.subscription.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final UserFeedZSetService userFeedZSetService;
    private final SubscriptionService subscriptionService;

    @Transactional
    @KafkaListener(topics = "new-posts", groupId = "feed-service")
    public void handleNewPost(NewPostEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Received new post event with id: {}", event.getPostId());
            Post post = postRepository.findById(event.getPostId())
                    .orElseThrow(() -> new EntityNotFoundException("Post not found: " + event.getPostId()));
            if (!post.canBeAddedToFeed()) {
                log.info("Post {} cannot be added to feed, skipping", event.getPostId());
                acknowledgment.acknowledge();
                return;
            }
            cachePost(post);
            processFeedUpdates(post);
            acknowledgment.acknowledge();
            log.info("Successfully processed new post event for post {}", event.getPostId());
        } catch (Exception e) {
            log.error("Error processing new post event for post {}", event.getPostId(), e);
            throw new KafkaException("Failed to process new post event", e);
        }
    }

    private void processFeedUpdates(Post post) {
        Set<Long> subscriberIds = subscriptionService.getSubscriberIds(post);
        if (!subscriberIds.isEmpty()) {
            List<Long> subscribersList = new ArrayList<>(subscriberIds);
            for (int i = 0; i < subscribersList.size(); i += 1000) {
                int end = Math.min(i + 1000, subscribersList.size());
                FeedUpdateEvent batchEvent = FeedUpdateEvent.builder()
                        .postId(post.getId())
                        .authorId(post.getAuthorId())
                        .subscriberIds(subscribersList.subList(i, end))
                        .build();
                try {
                    processIndividualFeedUpdate(batchEvent);
                } catch (Exception e) {
                    log.error("Error processing feed update batch for post {}", post.getId(), e);
                }
            }
        } else {
            log.info("No subscribers found for post {}", post.getId());
        }
    }

    private void processIndividualFeedUpdate(FeedUpdateEvent batchEvent) {
        for (Long subscriberId : batchEvent.getSubscriberIds()) {
            try {
                userFeedZSetService.addPostToFeed(
                        subscriberId,
                        batchEvent.getPostId(),
                        LocalDateTime.now()
                );
            } catch (Exception e) {
                log.error("Failed to add post {} to feed of user {}",
                        batchEvent.getPostId(), subscriberId, e);
            }
        }
    }

    private void cachePost(Post post) {
        PostCache postCache = PostCache.builder()
                .id(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .content(post.getContent())
                .likesCount(0L)
                .commentsCount(0L)
                .lastComments(new LinkedHashSet<>())
                .verified(post.isVerified())
                .published(post.isPublished())
                .visibility(post.getVisibility())
                .build();

        postCacheRepository.save(postCache);
        log.debug("Cached post {}", post.getId());
    }
}