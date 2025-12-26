package faang.school.postservice.service.post;

import faang.school.postservice.event.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchPublishingService {

    private final PostRepository postRepository;
    private final PostEventPublisher postEventPublisher;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishBatch(List<Post> batch) {
        log.debug("Publishing a batch of {} posts in the stream {}",
                batch.size(), Thread.currentThread().getName());

        try {
            batch.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });

            postRepository.saveAll(batch);
            log.debug("The batch of {} posts was successfully published", batch.size());

            for (Post post : batch) {
                PostEvent postEvent = PostEvent.builder()
                        .postId(post.getId())
                        .authorId(post.getAuthorId())
                        .content(post.getContent())
                        .followersIds(subscriptionRepository.findFollowerIdsByFolloweeId(post.getAuthorId()))
                        .createdAt(LocalDateTime.now())
                        .build();

                postEventPublisher.publish(postEvent);
                log.debug("Post {} has been sent to message broker", post.getId());
            }

        } catch (Exception e) {
            log.error("Error publishing batch of {} posts: {}", batch.size(), e.getMessage());
        }
    }
}