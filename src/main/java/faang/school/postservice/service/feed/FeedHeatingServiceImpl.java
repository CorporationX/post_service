package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeatingServiceImpl implements FeedHeatingService {
    private final UserServiceClient userServiceClient;
    private final FeedHeatingProducer feedHeatingProducer;

    @Qualifier("taskSplitterExecutor")
    private final ExecutorService taskSplitterExecutor;

    @Value("${app.heating.batch-size}")
    private int batchSize;

    @Value("${app.heating.max-pages}")
    private int maxPages;

    private static final int MAX_QUEUE_SIZE = 10000;

    @Override
    public boolean submitHeatingJob() {
        if (((ThreadPoolExecutor) taskSplitterExecutor).getQueue().size() > MAX_QUEUE_SIZE) {
            log.warn("Task splitter queue is full. Rejecting request");
            return false;
        }

        taskSplitterExecutor.submit(() -> {
            try {
                int page = 0;
                int totalUsers = 0;
                while (page < batchSize) {
                    List<Long> userIds = userServiceClient.getUserIdsBatch(page, batchSize);
                    if (userIds == null || userIds.isEmpty()) {
                        log.debug("No more users to fetch at page {}.", page);
                        break;
                    }

                    userIds.forEach(feedHeatingProducer::sendHeatingTask);
                    totalUsers += userIds.size();
                    log.debug("Sent batch {} with {} users for heating", page, userIds.size());
                    page++;
                }
                log.info("Feed heating initialization completed. Total users: {}", totalUsers);
            } catch (FeignException e) {
                log.error("User service unavailable during feed heating initialization", e);
            } catch (Exception e) {
                log.error("Error during feed heating initialization", e);
            }
        });

        return true;
    }
}
