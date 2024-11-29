package faang.school.postservice.listener;

import com.google.protobuf.InvalidProtocolBufferException;
import faang.school.postservice.protobuf.generate.FeedEventProto;
import faang.school.postservice.service.cache.AsyncCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostNewsFeedListener implements KafkaEventListener<byte[]> {

    private final AsyncCacheService<Long, Long> asyncCacheFeedRepository;

    @Override
    @KafkaListener(topics = "${spring.kafka.topics.post-for-feed.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "messageListenerContainer")
    public void onMessage(byte[] byteFeedEvent, Acknowledgment acknowledgment) {
        try {
            FeedEventProto.FeedEvent feedEvent = FeedEventProto.FeedEvent.parseFrom(byteFeedEvent);
            List<Long> followerIds = feedEvent.getFollowerIdsList();
            Long postId = feedEvent.getPostId();

            CompletableFuture<?>[] completableFutures = followerIds.stream()
                    .map(followerId -> asyncCacheFeedRepository.save(followerId, postId))
                    .toArray(CompletableFuture[]::new);

            CompletableFuture.allOf(completableFutures).join();
            acknowledgment.acknowledge();

        } catch (InvalidProtocolBufferException exception) {
            log.error("Error while parsing feedEvent", exception);
            throw new RuntimeException(exception);
        }
    }
}
