package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.subscription.SubscriptionUserIdDto;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostEventService implements KafkaReceivedEventService<PostEventDto> {
    private final KafkaPostProducer producer;
    private final KafkaSerializer kafkaSerializer;
    private final UserServiceClient userServiceClient;
    private final FeedRepository feedRepository;
    private final RedisFeedRepository redisFeedRepository;

    @Value("${feed.feed.cache.max-cache-size}")
    private final int maxCacheSize;

    @Value("${spring.data.kafka.producer.producers.post.limit-amount-user-ids}")
    private final int limitAmountUserIds;

    /* TODO:
    Во время ручного теста тут вылезло в Postmane: "No bean named 'kafkaProducerExecutor' available: No matching
    Executor bean found for qualifier 'kafkaProducerExecutor' - neither qualifier match nor bean name match!"
    подумать как такое не кидать пользователю
     */
    //@Async("kafkaProducerConsumerExecutor") почему-то если сделать, то будет ошибка
    // TODO: спросить вопрос что делать, если в процессе публикации в кафка возникла какая-то ошибка
    // как потом искать эти фиды
    public void produceToBroker(PostDto postDto) {
        Long followeeId = postDto.authorId();
        if (followeeId == null) {
            return;
        }

        List<SubscriptionUserIdDto> followerDtos;
        long lastId = 0L;
        do {
            //followerDtos = userServiceClient.getFollowerIds(followeeId, lastId, limitAmountUserIds);
            followerDtos = new ArrayList<>(Arrays.asList(
                new SubscriptionUserIdDto(2L),
                new SubscriptionUserIdDto(3L),
                new SubscriptionUserIdDto(4L)
            ));

            if (followerDtos.isEmpty()) {
                break;
            }
            List<Long> followerIds = followerDtos.stream()
                .map(SubscriptionUserIdDto::followerId)
                .toList();
            lastId = followerIds.get(followerIds.size() - 1);

            PostEventDto eventDto = createPostEventDto(followeeId, postDto, followerIds);
            String event = kafkaSerializer.serialize(eventDto);
            producer.send(event);
            followerDtos.clear();
        } while(!followerDtos.isEmpty());
    }

    // TODO подумать что делать, если за время, пока сообщение шло по кафке пост успел удалиться
    @Retryable(
        retryFor = {OptimisticLockException.class},
        maxAttemptsExpression = "${feed.feed.retry.max-attempts}",
        backoff = @Backoff(delayExpression = "${feed.feed.retry.delay}")
    )
    @Transactional
    @Override
    public void receiveFromBroker(PostEventDto event) {
        long postId = event.postId();

        for (Long followerId : event.followerIds()) {
            Optional<FeedCache> feedOpt = redisFeedRepository.findById(followerId);
            FeedCache feedCache = feedOpt.orElseGet(() -> FeedCache.builder()
                .version(0L)
                .id(followerId)
                .postIds(new LinkedHashSet<>())
                .build());
            feedCache.getPostIds().add(postId);

            if (feedCache.getPostIds().size() > maxCacheSize) {
                Iterator<Long> postIterator = feedCache.getPostIds().iterator();
                postIterator.next();
                postIterator.remove();
            }
            Feed feed = Feed.builder()
                .userId(followerId)
                .post(Post.builder().id(postId).build())
                .build();
            feedRepository.save(feed);
            redisFeedRepository.save(feedCache);
        }
    }


    private PostEventDto createPostEventDto(Long followeeId, PostDto postDto, List<Long> followerIds) {
        return PostEventDto.builder()
            .authorId(followeeId)
            .postId(postDto.id())
            .followerIds(followerIds)
            .build();
    }
}
