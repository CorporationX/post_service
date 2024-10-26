package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentDeleteEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedLikeEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostDeleteEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostEvent;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedUnlikeEvent;
import faang.school.postservice.producer.KafkaCommentDeleteProducer;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.producer.KafkaPostDeleteProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaUnlikeProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedEventService {
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaPostDeleteProducer kafkaPostDeleteProducer;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final KafkaCommentDeleteProducer kafkaCommentDeleteProducer;
    private final KafkaLikeProducer kafkaLikeProducer;
    private final KafkaUnlikeProducer kafkaUnlikeProducer;
    @Value("${feed.kafka.subscribers-batch-size}")
    private int subscribersBatchSize;
    @Value("${spring.data.kafka.topics.post.name}")
    String postTopic;
    @Value("${spring.data.kafka.topics.feed-heater.name}")
    private String feedHeaterTopic;

    @Async("feedExecutor")
    public void createAndSendFeedPostEventForNewPost(Long postId, Long authorId, LocalDateTime publishedAt) {
        createAndSendFeedPostEvent(postId, authorId, publishedAt, postTopic);
    }

    @Async("feedExecutor")
    public void createAndSendFeedPostEventForFeedHeater(Long postId, Long authorId, LocalDateTime publishedAt) {
        createAndSendFeedPostEvent(postId, authorId, publishedAt, feedHeaterTopic);
    }

    private void createAndSendFeedPostEvent(Long postId, Long authorId, LocalDateTime publishedAt, String topicName) {
        List<Long> subscribersIds = userServiceClient.getFollowerIdsByFolloweeId(authorId);

        if (subscribersIds == null || subscribersIds.isEmpty()) {
            log.info("Author {} has no subscribers or failed to retrieve subscribers. No events will be sent.", authorId);
        } else if (subscribersIds.size() <= subscribersBatchSize) {
            kafkaPostProducer.sendEventToTopic(new FeedPostEvent(postId, authorId, publishedAt, subscribersIds), topicName);
            log.info("Sent FeedPostEvent for postId {} with {} subscribers", postId, subscribersIds.size());
        } else {
            List<List<Long>> batches = partitionList(subscribersIds, subscribersBatchSize);

            int batchNumber = 0;
            for (List<Long> batch : batches) {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, publishedAt, batch);

                String messageKey = postId + "-" + batchNumber;

                kafkaPostProducer.sendEventToTopic(event, messageKey, topicName);

                log.info("Sent FeedPostEvent for postId {} batch {} with {} subscribers", postId, batchNumber, batch.size());

                batchNumber++;
            }
        }
    }


    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        int totalSize = list.size();
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, totalSize)));
        }
        return partitions;
    }

    @Async("feedExecutor")
    public void createAndSendFeedCommentEvent(FeedCommentEvent feedCommentEvent) {
        kafkaCommentProducer.sendEvent(feedCommentEvent);
        log.info("Sent FeedCommentEvent for postId {}", feedCommentEvent.getPostId());
    }

    @Async("feedExecutor")
    public void createAndSendFeedLikeEvent(long postId) {
        kafkaLikeProducer.sendEvent(new FeedLikeEvent(postId));
    }

    @Async("feedExecutor")
    public void createAndSendFeedPostDeletedEvent(long postId) {
        kafkaPostDeleteProducer.sendEvent(new FeedPostDeleteEvent(postId));
    }

    public void createAndSendFeedUnlikeEvent(Long postId) {
        kafkaUnlikeProducer.sendEvent(new FeedUnlikeEvent(postId));
    }

    public void createAndSendFeedCommentDeleteEvent(FeedCommentDeleteEvent event) {
        kafkaCommentDeleteProducer.sendEvent(event);
    }
}