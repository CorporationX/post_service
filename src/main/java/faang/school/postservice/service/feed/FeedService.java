package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostEvent;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    @Value("${feed.kafka.subscribers-batch-size}")
    private int subscribersBatchSize;

    @Async("feedExecutor")
    public void createAndSendFeedPostEvent(Long postId, Long authorId) {
        List<Long> subscribersIds = userServiceClient.getFollowerIdsByFolloweeId(authorId);

        if (subscribersIds.isEmpty()) {
            log.info("Author {} has no subscribers. No events will be sent.", authorId);
            return;
        } else if (subscribersIds.size() <= subscribersBatchSize) {
            kafkaPostProducer.sendEvent(new FeedPostEvent(postId, authorId, subscribersIds));
        } else {
            List<List<Long>> batches = partitionList(subscribersIds, subscribersBatchSize);

            int batchNumber = 0;
            for (List<Long> batch : batches) {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, batch);

                String messageKey = postId + "-" + batchNumber;

                kafkaPostProducer.sendEvent(event, messageKey);

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
}