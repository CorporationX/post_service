package faang.school.postservice.service.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.event.BanedUserEvent;
import faang.school.postservice.publisher.BanedUserEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.BanUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BanUserServiceImpl implements BanUserService {
    private static final int SIZE_OF_BATCH = 1000;
    private static final int NUMBER_OF_THREADS = 4;
    private static final long SYSTEM_USER_ID = -1L;

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final BanedUserEventPublisher banedUserEventPublisher;

    @Override
    @Scheduled(cron = "${scheduler.cron.ban-users}")
    public void banUsers() {
        Long maxUserId = userServiceClient.getMaxUserId();
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (long minId = 1; minId < maxUserId; minId += SIZE_OF_BATCH) {
            long maxId = Math.min(minId + SIZE_OF_BATCH - 1, maxUserId);
            long finalMinId = minId;

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    userContext.setUserId(SYSTEM_USER_ID);
                    List<Long> authorIdsToBan = postRepository.findAuthorsWithMoreThanFiveUnverifiedPostsInRange(finalMinId, maxId);
                    authorIdsToBan.forEach(authorId -> {
                        log.info("Sending authorId {} to Redis", authorId);
                        synchronized (banedUserEventPublisher) {
                            banedUserEventPublisher.publish(new BanedUserEvent(authorId));
                        }
                    });
                } catch (Exception e) {
                    log.error("While checking and banning users from id {} to {} an exception occurred", finalMinId, maxId, e);
                } finally {
                    userContext.clear();
                }
            }, executor);

            futures.add(future);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allFutures.join();

        executor.shutdown();
    }
}

