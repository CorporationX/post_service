package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.user.UserFollowersDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.FeedUpdateEvent;
import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.RedisAuthor;
import faang.school.postservice.model.RedisPost;
import faang.school.postservice.publisher.FeedUpdateEventPublisher;
import faang.school.postservice.repository.RedisAuthorRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.validator.CacheServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisPostRepository redisPostRepository;
    private final CachePostMapper cachePostMapper;
    private final RedisAuthorRepository redisAuthorRepository;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheServiceValidator validator;
    private final FeedUpdateEventPublisher feedUpdateEventPublisher;

    @Value("${spring.data.redis.post-cache.key-prefix}")
    private String postCacheKeyPrefix;

    @Value("${spring.data.redis.post-cache.field.views}")
    private String viewsField;

    public void publishPostAndAuthorAtRedis(Post post) {
        publishPostAuthor(post);
        publishPostAtRedis(post);
    }

    public void publishPostAtRedis(Post post) {
        RedisPost redisPost = cachePostMapper.toCache(post);
        redisPostRepository.save(redisPost);

        sendFeedUpdateMessage(post);
    }

    public void publishPostAuthor(Post post) {
        RedisAuthor redisAuthor = new RedisAuthor();
        redisAuthor.setId(post.getAuthorId());

        UserDto userDto = userServiceClient.getUserById(post.getAuthorId());
        redisAuthor.setName(userDto.getUsername());

        redisAuthorRepository.save(redisAuthor);
    }

    public void publishCommentAuthor(Long authorId) {
        Optional<RedisAuthor> optionalRedisAuthor = redisAuthorRepository.findById("User: " + authorId);

        if (optionalRedisAuthor.isEmpty()) {
            RedisAuthor redisAuthor = new RedisAuthor();
            redisAuthor.setId(authorId);
            UserDto userDto = userServiceClient.getUserById(authorId);
            redisAuthor.setName(userDto.getUsername());
        }
    }

    public void updatePostAtRedis(Post post) {
        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(post.getId());

        if (optionalRedisPost.isPresent()) {
            RedisPost redisPost = optionalRedisPost.get();
            cachePostMapper.update(post, redisPost);
            redisPostRepository.save(redisPost);
        }
    }

    //я не совсем понимаю как обеспечить целостность данных для нижних 3 методов
    //так например при добавлении нового коммента к посту 2 сервиса могут
    //перетереть данный друг друга вызвав метод save как этого избежать ?
    public void addLikeToCommentOrPost(LikeEvent likeEvent) {
        validator.validateLikeEvent(likeEvent);

        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(likeEvent.getPostId());
        if (optionalRedisPost.isPresent()) {

            RedisPost redisPost = optionalRedisPost.get();

            if (likeEvent.getCommentId() > 0) {
                for (CommentEvent commentEvent : redisPost.getCommentEvents()) {

                    if (commentEvent.getId() == likeEvent.getCommentId()) {
                        commentEvent.getLikeIds().add(likeEvent.getLikeId());
                        break;
                    }

                }

                redisPostRepository.save(redisPost);
            } else {
                redisPost.setLikes(redisPost.getLikes() + 1);
            }
        }

    }

    public void addNewViewToPost(PostViewEvent postViewEvent) {
        String key = generateCachePostKey(postViewEvent.getPostId());

        redisTemplate.opsForHash().increment(key, viewsField, 1);

    }

    public void addNewCommentToPost(CommentEvent commentEvent) {
        Optional<RedisPost> optionalRedisPost = redisPostRepository.findById(commentEvent.getPostId());

        if (optionalRedisPost.isPresent()) {

            RedisPost redisPost = optionalRedisPost.get();

            redisPost.getCommentEvents().add(commentEvent);

            redisPostRepository.save(redisPost);
        }

    }

    public void deletePostFromRedis(long postId) {
        redisPostRepository.deleteById(postId);
    }

    private String generateCachePostKey(Long postId) {
        return postCacheKeyPrefix + postId;
    }

    private void sendFeedUpdateMessage(Post post) {
        UserFollowersDto userFollowersDto = userServiceClient.getUserFollowersById(post.getAuthorId());

        FeedUpdateEvent feedUpdateEvent = new FeedUpdateEvent();

        List<Long> followersIds = userFollowersDto.getFollowersIds();

        for (Long followersId : followersIds) {
            feedUpdateEvent.addUser(followersId);

            if (feedUpdateEvent.listIsFull()){
                feedUpdateEventPublisher.sendMessage(feedUpdateEvent);

                feedUpdateEvent.clearList();
            }
        }

        feedUpdateEventPublisher.sendMessage(feedUpdateEvent);
    }
}
