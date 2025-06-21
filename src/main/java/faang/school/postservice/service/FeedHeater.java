package faang.school.postservice.service;

import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.publisher.KafkaHeatFeedEventPublisher;
import faang.school.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeater {
    private final UserRepository userRepository;
    private final KafkaHeatFeedEventPublisher publisher;

    @Value("${spring.data.thread-pool.heater-feed-size}")
    private int poolSize;

    public void heatFeedCache() throws InterruptedException {
        log.debug("Запуск прогрева кеша");

        List<Long> usersIds = userRepository.findAllUsersIds();

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        for (Long userId : usersIds) {
            executorService.submit(() -> {
                try {
                    publisher.publish(new FeedHeatEvent(userId));
                } catch (Exception e) {
                    log.error("Ошибка при публикации события для пользователя {}: {}", userId, e.getMessage(), e);
                }
            });
        }

        executorService.awaitTermination(5, TimeUnit.MINUTES);
        executorService.shutdown();
    }
}
