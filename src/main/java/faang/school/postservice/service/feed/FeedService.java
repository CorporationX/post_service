package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostEventMapper;
import faang.school.postservice.mapper.user.UserEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.FeedEvent;
import faang.school.postservice.model.event.UserEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final static int POST_LIMIT = 20;
    private final static int FOLLOWEE_LIMIT = 50;

    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;
    private final PostEventMapper postEventMapper;
    private final UserServiceClient userServiceClient;
    private final UserEventMapper userEventMapper;

    public List<Post> getUserFeed(Long postId, Long userId) {
        if (userId == null) {
            throw new DataValidationException("Invalid request. User not specified");
        }
        //Достаем айдишники постов из редиса
        Set<Long> postIdsFromRedis = getPostIdsFromRedis(userId);

        //Если нет postId, то берем первые 20 (свежие)
        if (postId == null) {
            return getPosts(userId, postIdsFromRedis);
        } else {
            //Если есть postId, то достаем следующие за ним ids в кол-ве POST_LIMIT - 1 постов
            LinkedList<Long> ids = new LinkedList<>(postIdsFromRedis);
            ids.addFirst(postId);

            LinkedList<Long> sortedPostIds = ids.stream()
                    .filter(id -> id > postId)
                    .limit(POST_LIMIT - 1)
                    .collect(Collectors.toCollection(LinkedList::new));

            return getPosts(userId, sortedPostIds);
        }
    }

    private List<Post> getPostsByFolloweeId(Long userId, int postCount) {
        UserDto userDto = userServiceClient.getUser(userId);
        UserEvent userEvent = userEventMapper.toEvent(userDto);
        List<Long> followeesIds = userEvent.getFolloweesIds();

        return postRepository.findByAuthorsId(followeesIds, postCount);
    }

    private Set<Long> getPostIdsFromRedis(Long userId) {
        Set<Long> sortedIds = new LinkedHashSet<>();

        redisFeedRepository.findById(userId).ifPresentOrElse(
                f -> sortedIds.addAll(f.getPostsId().stream()
                        .sorted(Comparator.reverseOrder())
                        .limit(POST_LIMIT)
                        .collect(Collectors.toCollection(LinkedHashSet::new))),
                () -> {
                    FeedEvent feed = new FeedEvent();
                    feed.setPostsId(new TreeSet<>());
                });

        return sortedIds;
    }

    private List<Post> getPosts(Long userId, Collection<Long> postIds) {
        //Достаем сначала из редиса посты по айдишникам
        List<Post> posts = new ArrayList<>(StreamSupport.stream(redisPostRepository.findAllById(postIds).spliterator(), false)
                .map(postEventMapper::toEntity)
                .toList());

        //Если нет 20 айдишников, то надо идти за оставшимися постами в БД (кол-во постов определяется разницов)
        if (postIds.size() < POST_LIMIT) {
            int requiredQuantityOfPosts = POST_LIMIT - postIds.size();
            posts.addAll(getPostsByFolloweeId(userId, requiredQuantityOfPosts));
        }
        return posts;
    }
}
