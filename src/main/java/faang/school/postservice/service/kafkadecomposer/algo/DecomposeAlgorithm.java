package faang.school.postservice.service.kafkadecomposer.algo;

import faang.school.postservice.dto.user.UserFeedDto;
import faang.school.postservice.publisher.kafka.KafkaPostPublisher;
import faang.school.postservice.threadpool.ThreadPoolForNewsFeedAlgo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class DecomposeAlgorithm {
    private final KafkaPostPublisher kafkaPostPublisher;
    private final ThreadPoolForNewsFeedAlgo threadPoolForNewsFeedAlgo;

    protected void startAlgo(List<UserFeedDto> userDtoList, Long postId, int batchSize) {

        int stepLength = (int) Math.floor((double) userDtoList.size() / batchSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            int start = i * stepLength;
            int end = (i + 1) * stepLength;

            if (i == batchSize - 1) {
                end = userDtoList.size();
            }

            int finalEnd = end;

            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
                            kafkaPostPublisher.sendMessage(userDtoList.subList(start, finalEnd), postId),
                    threadPoolForNewsFeedAlgo.newsFeedAloPool()
            );

            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during comment moderation", e);
        } catch (TimeoutException e) {
            futures.forEach(future -> future.cancel(true));
            log.warn("Execution time exceeded, threads were forcibly closed.");
        }
    }
}
