package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.redis.PostIdDto;
import faang.school.postservice.mapper.RedisPostMapper;
import faang.school.postservice.mapper.RedisUserMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    @Value("${feed.batch_size}")
    private int postFeedBatch = 20;
    @Value("${feed.max_size}")
    private int maxFeedSize = 500;

    public List<FeedDto> getFeed(Long lastPostId) {
        long userId = userContext.getUserId();
        final List<PostIdDto> nextPostIdDtosBatch = new ArrayList<>();

        redisFeedRepository.findById(userId)
                .ifPresent((redisFeed) -> {
                    List<PostIdDto> nextPostIdDtosBatch1 = getNextPostIdDtosBatch(lastPostId, redisFeed.getPostIds());
                    nextPostIdDtosBatch.addAll(nextPostIdDtosBatch1);
                });

        if (nextPostIdDtosBatch.isEmpty()) {
            //from BD
        }

        return nextPostIdDtosBatch.stream()
                .map(postIdDto -> buildFeedDto(getRedisUser(userId), getRedisPost(postIdDto.getPostId())))
                .toList();
    }

    private List<PostIdDto> getNextPostIdDtosBatch(Long lastPostId, SortedSet<PostIdDto> postIdDtos) {
        if (lastPostId != null) {
            Optional<PostIdDto> postIdDtoOpt = postIdDtos.stream()
                    .filter(postIdDto -> postIdDto.getPostId() == lastPostId)
                    .findFirst();

            if (postIdDtoOpt.isPresent()) {
                return getNextPostIdDtos(postIdDtos, postIdDtoOpt.get());
            }
        }

        if (lastPostId == null) {
            return getNextPostIdDtos(postIdDtos, null);
        }

        return new ArrayList<>();
    }

    private List<PostIdDto> getNextPostIdDtos(SortedSet<PostIdDto> postIdDtos, PostIdDto fromThisPost) {
        if (fromThisPost == null) {
            return setToListWithLimit(postIdDtos);
        }
        //set tailed and unnecessary first element (fromThisPost from argument) deleted
        postIdDtos.tailSet(fromThisPost).remove(postIdDtos.first());
        return setToListWithLimit(postIdDtos);
    }

    private List<PostIdDto> setToListWithLimit(SortedSet<PostIdDto> postIdDtos) {
        return postIdDtos.stream().limit(postFeedBatch).toList();
    }

    private RedisPost getRedisPost(long postId) {
        return redisPostRepository.findById(postId)
                .orElseGet(() -> redisPostMapper.toEntity(postService.getPostDto(postId)));
    }

    private RedisUser getRedisUser(long userId) {
        return redisUserRepository.findById(userId)
                .orElseGet(() -> redisUserMapper.toEntity(userServiceClient.getUser(userId)));
    }

    private FeedDto buildFeedDto(RedisUser redisUser, RedisPost redisPost) {
        return FeedDto.builder()
                .userId(redisUser.getId())
                .username(redisUser.getUsername())
                .postId(redisPost.getId())
                .content(redisPost.getContent())
                .likes(redisPost.getLikes())
                .comments(redisPost.getComments().stream().toList())
                .publishedAt(redisPost.getPublishedAt())
                .updatedAt(redisPost.getUpdatedAt())
                .build();
    }
}
