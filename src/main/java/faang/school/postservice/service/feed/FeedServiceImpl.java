package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService{

    private final KafkaFeedHeatEventProducer kafkaFeedHeatEventProducer;
    private final UserServiceClient userServiceClient;
    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    @Override
    public void initializeFeedHeat() {
        int lastBatchSize = batchSize;
        long startingFromId = 0;
        do {
            // Add retry, timeout, try/catch
            List<Long> userIds = userServiceClient.getUserIdsByBatch(batchSize, startingFromId);
            startingFromId = userIds.get(userIds.size() - 1);
            lastBatchSize = userIds.size();
            kafkaFeedHeatEventProducer.sendMessage(userIds);
        } while(lastBatchSize == batchSize);
    }
}