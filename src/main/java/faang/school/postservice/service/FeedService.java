package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.cache.Post;
import faang.school.postservice.model.cache.User;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    @Value("${feed.page-size:20}")
    private int feedPageSize;

    private final UserContext userContext;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    public List<PostDto> getFeed(Long lastPostId) {
        long subscriberId = userContext.getUserId();
        log.info("Getting feed for subscriberId={} beginning with lastPostId={}",
                subscriberId, lastPostId);

        Set<Long> postIds;
        if (lastPostId == null) {
            log.info("Getting first page ({} posts) of feed", feedPageSize);
            postIds = new TreeSet<>(redisFeedRepository
                    .getLimitPostIdsBySubscriberId(
                            subscriberId,
                            feedPageSize));
        } else {
            log.info("Getting next page of feed");
            postIds = new TreeSet<>(redisFeedRepository
                    .getLimitPostIdsBySubscriberIdAfterLastPostId(
                            subscriberId,
                            lastPostId,
                            feedPageSize));
        }

        log.info("Got {} posts from cache", postIds.size());
        if (postIds.size() < feedPageSize) {
            log.info("Fetching postIds from database");
            Set<Long> dbPostIds = postRepository.findPostIds(subscriberId,
                    lastPostId, feedPageSize - postIds.size());
            postIds.addAll(dbPostIds);
        }

        log.info("Building post DTOs & returning feed");
        return postIds.stream()
                .map(this::buildPostDto)
                .toList();
    }

    private PostDto buildPostDto(Long postId) {
        PostDto postDto;
        Long authorId;
        faang.school.postservice.model.Post dbPost;

        log.info("Fetching post with id: {} from cache...", postId);
        Optional<Post> cachePost = redisPostRepository.findById(postId);

        if (cachePost.isPresent()) {
            log.info("Post with id: {} found in cache!", postId);
            authorId = cachePost.get().getAuthorId();
            postDto = postMapper.toPostDto(cachePost.get());
        } else {
            log.info("Post with id: {} not found in cache! Fetching from DB...", postId);
            dbPost = postRepository.findById(postId).orElseThrow(() -> {
                log.error("Post with id: {} not found in DB!", postId);
                return new EntityNotFoundException(
                        String.format("Post with id: %d not found!", postId));
            });
            log.info("Post with id: {} found in DB!", postId);
            authorId = dbPost.getAuthorId();
            postDto = postMapper.toPostDto(dbPost);
        }

        log.info("Fetching author with id: {} from cache...", authorId);
        Optional<User> author = redisUserRepository.findById(authorId);
        UserDto userDto;

        if (author.isPresent()) {
            log.info("Author with id: {} found in cache!", authorId);
            userDto = userMapper.toUserDto(author.get());
        } else {
            log.info("Author with id: {} not found in cache! Fetching from DB...", authorId);
            userDto = userServiceClient.getUser(authorId);
        }

        log.info("Building post DTO to return...");
        return new PostDto(
                postDto.id(),
                postDto.content(),
                userDto,
                postDto.likesCount(),
                postDto.commentsCount(),
                postDto.viewsCount(),
                postDto.publishedAt(),
                postDto.updatedAt()
        );
    }
}
