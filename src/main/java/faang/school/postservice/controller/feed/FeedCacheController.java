package faang.school.postservice.controller.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.feed.FeedHeatingProducer;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("api/v1/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedCacheController {
    private final UserServiceClient userServiceClient;
    private final FeedHeatingProducer feedHeatingProducer;

    @Qualifier("taskSplitterExecutor")
    private final ExecutorService taskSplitterExecutor;

    @Value("${app.heating.batch-size}")
    private int batchSize;

    @Value("${app.heating.max-pages:1000}")
    private int maxPages;

    private static final int MAX_QUEUE_SIZE = 10000;

    @PostMapping("/heat")
    public ResponseEntity<String> heatFeedCache() {
        if (((ThreadPoolExecutor) taskSplitterExecutor).getQueue().size() > MAX_QUEUE_SIZE) {
            log.warn("Task splitter queue is full. Rejecting request");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Heating system overloaded");
        }

        taskSplitterExecutor.submit(() -> {
            try {
                int page = 0;
                int totalUsers = 0;

                while (page < maxPages) {
                    List<Long> userIds = userServiceClient.getUserIdsBatch(page, batchSize);
                    if (userIds == null || userIds.isEmpty()) break;

                    userIds.forEach(feedHeatingProducer::sendHeatingTask);
                    totalUsers += userIds.size();

                    log.debug("Sent batch {} with {} users for heating", page, userIds.size());
                    page++;
                }

                log.info("Feed heating initialization completed. Total users: {}", totalUsers);
            } catch (FeignException e) {
                log.error("User service unavailable during feed heating initialization", e);
            } catch (Exception e) {
                log.error("Critical error during feed heating initialization", e);
            }
        });

        return ResponseEntity.accepted().body("Feed heating process started successfully");
    }
}
