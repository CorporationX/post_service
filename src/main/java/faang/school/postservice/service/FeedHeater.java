package faang.school.postservice.service;

import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.publisher.KafkaHeatFeedEventPublisher;
import faang.school.postservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;


@Service
@Slf4j
public class FeedHeater {
    private final UserRepository userRepository;
    private final KafkaHeatFeedEventPublisher publisher;
    private final ExecutorService executorService;


    public FeedHeater(UserRepository userRepository,
                      KafkaHeatFeedEventPublisher publisher,
                      @Qualifier("feedHeaterExecutor") ExecutorService executorService) {
        this.userRepository = userRepository;
        this.publisher = publisher;
        this.executorService = executorService;
    }

    public void heatFeedCache() throws InterruptedException {
        log.debug("Запуск прогрева кеша");

        List<Long> usersIds = userRepository.findAllUsersIds();


        for (Long userId : usersIds) {
            executorService.submit(() -> {
                try {
                    publisher.publish(new FeedHeatEvent(userId));
                } catch (Exception e) {
                    log.error("Ошибка при публикации события для пользователя {}: {}", userId, e.getMessage(), e);
                }
            });
        }

    }
}
