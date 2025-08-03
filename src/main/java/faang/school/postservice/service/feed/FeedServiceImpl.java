package faang.school.postservice.service.feed;

import faang.school.postservice.cache.RedisCache;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.UserFeedHeatDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.kafka.KafkaPostEventDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import faang.school.postservice.mapper.KafkaPostEventToRedisPostMapper;
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
    private final KafkaPostEventToRedisPostMapper kafkaPostEventToRedisPostMapper;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;

    // PostConstruct?
    // Add retry, timeout, try/catch
    @Override
    public void initializeFeedHeat() {
        int lastBatchSize = batchSize;
        long startingFromId = 0;
        do {
            List<UserFeedHeatDto> userFeedHeatDtos = userServiceClient.getUserIdsByBatch(batchSize, startingFromId);
            startingFromId = userFeedHeatDtos.get(userFeedHeatDtos.size() - 1).userId();
            lastBatchSize = userFeedHeatDtos.size();
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
        // тут использовать тот же запрос что и в addCreatedPostToSubscribers();
        // Для каждого пользователя и получится мешать их батчами в кафку если возвращаемый батч был меньше стандарта, обработать сразу тут
    }

    public void addCreatedPostToSubscribers(KafkaPostEventDto eventDto) {
        RedisPostDto redisPostDto = kafkaPostEventToRedisPostMapper.postEventToRedisPostDto(eventDto);
        cache.putPost(redisPostDto);

        // надо бы добавить новый запрос - на получение батчами ид подписчиков для пользователя.
        List<Long> subscriberIds = userServiceClient.getFollowers(eventDto.authorId())
                .stream()
                .map(UserDto::id)
                .toList();

        putUserIntoCache(eventDto.authorId());
        // воспользоваться тут той же логикой, что и для распределения по подписчикам в fillCacheForUsers();
        subscriberIds.forEach(followerId ->
                cache.putFeed(followerId, redisPostDto.getId(), redisPostDto.getCreatedAt()));
    }

    public void putUserIntoCache(long userId) {
        // Исправить на нормального юзера.
        // Вместо своих дто, дополнить UserDto?
        cache.putUser(userServiceClient.getUser(userId).id());
    }

    @Override
    public void updateCommentInCache(KafkaCommentEventDto dto) {
        cache.addCommentToPostCache(dto);
    }

    @Override
    public void updateViewsForPost(long postId){
        cache.updatePostViews(postId);
    }
}