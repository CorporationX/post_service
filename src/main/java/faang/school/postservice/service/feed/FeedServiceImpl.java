package faang.school.postservice.service.feed;

import faang.school.postservice.cache.RedisCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.UserFeedHeatDto;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final KafkaFeedHeatEventProducer kafkaFeedHeatEventProducer;
    private final UserServiceClient userServiceClient;
    private final RedisCache cache;
    private final PostRepository postRepository;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    // PostConstruct?
    // Add retry, timeout, try/catch
    @Override
    public void initializeFeedHeat() {
        log.info("Initializing feed heat");
        int lastBatchSize = batchSize;
        long startingFromId = 0;
        do {
            log.info("DO: lastBatchSize = {}, startingFromId = {}", lastBatchSize, startingFromId);
            List<UserFeedHeatDto> userFeedHeatDtos = userServiceClient.getUserIdsByBatch(batchSize, startingFromId);
            startingFromId = userFeedHeatDtos.get(userFeedHeatDtos.size() - 1).userId();
            lastBatchSize = userFeedHeatDtos.size();
            log.info("userFeedHeatDtos = {} | lastBatchSize = {}, startingFromId = {}", userFeedHeatDtos, lastBatchSize, startingFromId);
            kafkaFeedHeatEventProducer.sendMessage(userFeedHeatDtos);
        } while (lastBatchSize == batchSize);
    }

    public void fillCacheForUsers(List<UserFeedHeatDto> users) {
        // Заполнение будет происходить через пользователя и его подписчиков.
        // Кладём пользователя, проходимся по его подписчикам, им в фид выкидываем его посты(пускай последние 20-50)
        // по мере заполнения - фид будет переполнятся и за счёт score будут оставаться только самые свежие фиды?
        // а что если фоловеров несколько миллионов? надо разделить на батчи для кафки?
        users.forEach(dto -> {
            Long user = dto.userId();
            List<Long> followerIds = dto.followerIds();
            cache.putFeedForSubscribers(user, followerIds);
        });
    }
}