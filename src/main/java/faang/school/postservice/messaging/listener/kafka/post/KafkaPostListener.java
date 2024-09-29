package faang.school.postservice.messaging.listener.kafka.post;

import faang.school.postservice.event.kafka.PostKafkaEvent;
import faang.school.postservice.messaging.listener.kafka.KafkaEventListener;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostListener implements KafkaEventListener<PostKafkaEvent> {
    private final RedisFeedRepository redisFeedRepository;

    @Value("${spring.data.redis.cache.capacity.max.feed}")
    private long maxCapacityFeed;
    @Value("${spring.data.redis.cache.capacity.sublist}")
    private int sublistSize;

    @Override
    @KafkaListener(topics = "${spring.kafka.topic.posts}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(PostKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Post event received. Post ID: {}, Followers ID: {}",
                event.getPostId(), event.getFollowers());

        List<List<Long>> followersSublist = ListUtils.partition(event.getFollowers(), sublistSize);
        if (followersSublist.isEmpty()){
            log.warn("No followers to process for Post ID: {}", event.getPostId());
            acknowledgment.acknowledge();
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(Math.min(followersSublist.size(), 10));
        CountDownLatch latch = new CountDownLatch(followersSublist.size());

        for (List<Long> followersList : followersSublist) {
            try {
                executorService.submit(() -> {
                    try {
                        log.info("Post ID: {} to user process started", event.getPostId());
                        addPostInFollower(followersList, event.getPostId());
                    } finally {
                        latch.countDown();
                    }
                });
            } catch (Exception e){
                log.error("Error while submitting task for Post ID: {}", event.getPostId(), e);
                latch.countDown();
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("Thread interrupted while waiting for tasks to complete", e);
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    log.warn("Executor did not terminate in the specified time.");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                log.error("Thread interrupted while awaiting executor termination", e);
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        acknowledgment.acknowledge();
    }

    private void addPostInFollower(List<Long> followers, long postId) {
        followers.forEach(follower -> {
            redisFeedRepository.getAllPostIdsByFollowerId(follower).ifPresentOrElse(feedRedis -> {
                        if (feedRedis.getPostsIds().size() >= maxCapacityFeed) {
                            feedRedis.getPostsIds().pollFirst();
                            log.info("Old post from User ID: {} removed", feedRedis.getPostsIds());
                        }
                        feedRedis.getPostsIds().add(postId);
                        redisFeedRepository.save(feedRedis);
                        log.info("Post ID: {} added to User ID: {}", postId, feedRedis.getPostsIds());
                    },
                    () -> {
                        FeedRedis feedRedis = FeedRedis.builder()
                                .id(follower)
                                .postsIds(new TreeSet<>(Collections.singleton(postId)))
                                .build();
                        redisFeedRepository.save(feedRedis);
                        log.info("New feed created for follower with ID: {}", follower);
                    }
            );

        });
    }
}