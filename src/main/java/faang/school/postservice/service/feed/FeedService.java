package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.mapper.FeedMapper;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final UserContext userContext;
    private final FeedOtherService feedOtherService;

    @Value("${feed.feed.default-feed-amount}")
    private final int defaultFeedAmount;



    public List<FeedPostDto> getFeed(Long postId) {
        postId = postId == null ? 0 : postId;
        long userId = userContext.getUserId();
        Optional<FeedCache> cacheOpt = redisFeedRepository.findById(userId);
        // тут подумать надо ли делать заполнение кэша из бд
        // такая ситуация может возникнуть, только если redis отключился и в момент прихода запроса кэш ещё
        // наполняется, как вариант не накатывать приложение пока не заполнится кэш
        // пока кэш не наполняется из бд
        FeedCache cache = cacheOpt.orElseGet(() -> FeedCache.builder()
            .id(userId)
            .postIds(new LinkedHashSet<>())
            .version(0L)
            .build());
        List<Long> postIds = new ArrayList<>(cache.getPostIds());
        int amountFromDb = 0;
        long differenceSize = defaultFeedAmount - postIds.size();
        if (differenceSize > 0) {
            Long maxPostId = postIds.isEmpty() ? 0L : Collections.max(postIds);
            List<Feed> feeds = getFeedFromDb(userId, maxPostId, differenceSize);
            amountFromDb = feeds.size();
            List<Long> postIdsFromDb = feeds.stream()
                .map(feed -> feed.getPost().getId())
                .toList();
            postIds.addAll(postIdsFromDb);
        }
        List<FeedPostDto> feedDtos = new ArrayList<>();
        for (Long feedPostId : postIds) {
            FeedPostDto dto = collectFeedDto(feedPostId); // пока фиды генерятся последовательно
            feedDtos.add(dto);
        }

        log.info("Received {} feeds for user {}: {} feeds from cache, {} feeds from db",
            feedDtos.size(), userId, postIds.size(), amountFromDb
        );

        return feedDtos;
    }

    private List<Feed> getFeedFromDb(long userId, Long postId, long feedAmount) {
        return feedRepository.findFeedsByUserId(userId, postId, feedAmount);
    }

    private FeedPostDto collectFeedDto(long postId) {
        // TODO потом подумать как можно сделать через список/мапу добывателей соответствующих id
        CompletableFuture<List<Long>> commentIdsFuture = feedOtherService.getCommentIds(postId);
        CompletableFuture<List<Long>> likeIdsFuture = feedOtherService.getLikeIds(postId);
        CompletableFuture<List<Long>> postViewUserIdsFuture = feedOtherService.getPostViewUserIds(postId);

        try {
            return FeedPostDto.builder()
                .postId(postId)
                .commentIds(commentIdsFuture.get())
                .likeIds(likeIdsFuture.get())
                .postViewUserIds(postViewUserIdsFuture.get())
                .build();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
