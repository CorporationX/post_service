package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService{

    private final UserServiceClient userServiceClient;
    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    @Override
    public void initializeFeedHeat() {
        int lastBatchSize = batchSize;
        long startingFromId = 0;
        do {
            List<Long> userIds = userServiceClient.getUserIdsByBatch(batchSize, startingFromId);
            startingFromId = userIds.get(userIds.size() - 1);
            lastBatchSize = userIds.size();
            // Send to kafka as CompletableFuture batch after batch so Redis Heating woudn't wait for all the ids to be gathered first.
        } while(lastBatchSize < batchSize);
    }
}
