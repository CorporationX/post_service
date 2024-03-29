package faang.school.postservice.service;

import faang.school.postservice.dto.event.PostKafkaEventDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.RedisCashMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisCashMapper redisCashMapper;
    private final PostService postService;
    private final RedisCashService redisCashService;
    @Value("${feed.feed-size}")
    private int feedSize;
    @Value("${feed.view-size}")
    private int feedViewSize;

    public void fillFeed(PostKafkaEventDto event) {
        log.info("Start fill feed for event " + event);
        List<Long> users = event.getFollowers();
        for (Long user : users) {
            FeedCache feed = redisCashService.getOrCreateFeed(user);
            feed.getPostIds().add(event.getPostId());
            if (feed.getPostIds().size() > feedSize) {
                feed.getPostIds().remove(feed.getPostIds().iterator().next());
            }
            redisCashService.saveFeedCache(feed);
        }
        log.info("End fill feed for event " + event);
    }

    public TreeSet<PostFeedDto> getFeed(long userId, Long postId) {
        TreeSet<PostFeedDto> feed = new TreeSet<>();

        FeedCache feedCache = redisCashService.getFeed(userId).orElse(null);
        if (feedCache == null) {
            // Если лента в кэше не заполнена идем за ней в БД
            return getAndFillFeed(userId);
        }

        //// Формируем ленту из постов с учетом feedViewSize, начиная с postId
        // Перевод элементов в массив для дальнейшей выборки с сохранением порядка элементов как в LinkedHashSet
        Object[] array = feedCache.getPostIds().toArray();
        int lastIndex = array.length - 1;

        // Определяем firstSelectIndex - индекс поста от которого начнем выборку
        // В циклах делаем перебор элементов с конца, т.е. начиная с последних добавленных в LinkedHashSet
        int firstSelectIndex = lastIndex; // по умолчанию последний элемент, если postId == null
        if (postId != null) {
            for (int i = lastIndex; i >= 0; i--) {
                if (array[i] == postId) {
                    firstSelectIndex = i - 1; // -1 т.к. выбирать будем со следующего поста от найденного
                    break;
                }
            }
        }
        // Делаем выборку постов в количестве feedViewSize начиная с firstSelectIndex
        int feedViewSize = (firstSelectIndex - this.feedViewSize) < 0 ? 0 : (firstSelectIndex - this.feedViewSize);
        for (int i = firstSelectIndex; i >= feedViewSize; i--) {
            PostFeedDto postFeed = getPost((Long) array[i]);
            if (postFeed != null) {
                feed.add(postFeed);
            }
        }
        return feed;
    }

    private TreeSet<PostFeedDto> getAndFillFeed(long userId) {
        TreeSet<PostFeedDto> feed = new TreeSet<>();
        List<Post> posts = postService.getPostsByFollowee(userId);
        if (posts.isEmpty()) {
            throw new EntityNotFoundException("At this moment your news feed is empty");
        }
        redisCashService.createFeedCacheAsync(userId, posts);
        //При первом обращении заполняем и выводим ленту в рамках feedViewSize (limit(feedViewSize)).
        // При повторном обращении с заполненным postId лента будет формироваться уже из кэша, при 1 обращении его заполняем.
        posts.stream()
                .limit(feedViewSize)
                .map(post -> {
                    PostFeedDto postFeedDto = redisCashMapper.toPostFeedDto(post);
                    postFeedDto.setUserDto(getUser(post.getAuthorId()));
                    return postFeedDto;
                })
                .forEach(postFeedDto -> feed.add(postFeedDto));
        return feed;
    }


    private PostFeedDto getPost(long postId) {
        Optional<PostCache> cache = redisCashService.getPostCache(postId);

        if (cache.isPresent()) {
            PostCache postCache = cache.get();
            PostFeedDto postFeed = redisCashMapper.toPostFeedDto(postCache);
            postFeed.setUserDto(getUser(postCache.getAuthorId()));
            return postFeed;
        }

        try {
            Post post = postService.getPostById(postId);
            PostFeedDto postFeed = redisCashMapper.toPostFeedDto(post);
            postFeed.setUserDto(getUser(post.getAuthorId()));
            redisCashService.savePostCache(redisCashMapper.toCash(post));
            return postFeed;
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage() + postId, e);
            redisCashService.deletePostCache(postId); // добавить удаление из лент? или по событию об удалении поста?
            return null;
        }
    }

    private UserDto getUser(long userId) {
        Optional<UserCache> userCash = redisCashService.getUserCache(userId);
        if (userCash.isPresent()) {
            return redisCashMapper.toDto(userCash.get());
        }
        return redisCashService.getAndSaveUserCache(userId);
    }
}