package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.dto.post.PostCashDto;
import faang.school.postservice.dto.post.PostUiDto;
import faang.school.postservice.dto.user.UserCashDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostUiDtoMapper;
import faang.school.postservice.mapper.UserCashDtoMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.post.PostCashRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.repository.user.UserCashDtoCashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    @Value("${spring.newsfeed.size}")
    private int newsFeedMaxSize;

    @Value("${spring.newsfeed.page.size}")
    private int feedPageSize;

    private static final String NEWS_FEED_KEY_PREFIX = "newsfeed:";
    private final RedisTemplate<String, Long> redisNewsFeedTemplate;
    private final PostCashRepository postCashRepository;
    private final UserCashDtoCashRepository userCashDtoRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final PostUiDtoMapper postUiDtoMapper;
    private final UserCashDtoMapper userCashDtoMapper;

    public NewsFeed getNewsFeed(Long userId) {
        String key = getKey(userId);
        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, 0, -1);

        SortedSet<Long> posts = new TreeSet<>(Comparator.reverseOrder());
        if (postIds != null) {
            posts.addAll(postIds);
        }
        return new NewsFeed(userId, posts);
    }

    public List<PostUiDto> getNewsFeedContent(Long userId, Long lastPostId) {
        String key = getKey(userId);

        if(lastPostId == null) {
            Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().range(key, 0, feedPageSize-1);
            TreeSet<Long> reversedPostIds = new TreeSet<>(Comparator.reverseOrder());
            reversedPostIds.addAll(postIds);
            return convertToPostUiDtos(reversedPostIds);
        }
        Long  rank = redisNewsFeedTemplate.opsForZSet().rank(key, lastPostId);
        if(rank == null) {

            return getNewsFeedFromDb(Long.MAX_VALUE);
        }

        long start = rank + 1;
        long end = start + feedPageSize - 1;
        log.info("start = {}; end = {}; feedPageSize = {}",start, end, feedPageSize);
        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().range(key, start, end);
        TreeSet<Long> reversedPostIds = new TreeSet<>(Comparator.reverseOrder());
        reversedPostIds.addAll(postIds);
        //in progress
        if(reversedPostIds.size() < feedPageSize) {
            return getNewsFeedFromDb(reversedPostIds.last());
        }
        return convertToPostUiDtos(reversedPostIds);
    }

    public void addPostIdToUserNewsFeed(PostAndFollowersDto postAndFollowersDto) {
        for (Long follower : postAndFollowersDto.getFollowers()) {
            double score = -System.currentTimeMillis();
            String redisKey = getKey(follower);
            if (Boolean.TRUE.equals(redisNewsFeedTemplate.hasKey(redisKey))) {
                redisNewsFeedTemplate.opsForZSet().add(
                        redisKey,
                        postAndFollowersDto.getId(),
                        score);
                trimNewsFeed(redisKey, newsFeedMaxSize);
                log.info("Size of newsfeed: {}", redisNewsFeedTemplate.opsForZSet().size(redisKey));
            } else {
                redisNewsFeedTemplate.opsForZSet().add(
                        redisKey,
                        postAndFollowersDto.getId(),
                        score);
            }
        }
        log.info("NewsFeedService.addPostIdToUserNewsFeed() {}", postAndFollowersDto);
    }

    @Transactional
    public List<UserDto> getFollowees(Long userId) {
        return userServiceClient.getFollowees(userId);
    }

    private String getKey(Long userId) {
        return NEWS_FEED_KEY_PREFIX + userId;
    }

    private void trimNewsFeed(String redisKey, int size) {
        Long feedSize = redisNewsFeedTemplate.opsForZSet().size(redisKey);
        if (feedSize != null) {
            redisNewsFeedTemplate.opsForZSet().removeRange(redisKey, size, feedSize);
        }
    }

    private List<PostUiDto> convertToPostUiDtos(Set<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<PostUiDto> newsFeedPosts = new ArrayList<>();
        for (Long p : postIds) {
            Optional<PostCashDto> optionalPost = postCashRepository.findById(p);
            if (optionalPost.isPresent()) {
                PostUiDto postUiDto = new PostUiDto();
                Optional<UserCashDto> optionalUserCashDto = userCashDtoRepository.findById(optionalPost.get().getAuthorId());

                postUiDto.setId(p);
                postUiDto.setContent(optionalPost.get().getContent());
                optionalUserCashDto.ifPresent(postUiDto::setAuthor);
                postUiDto.setProjectId(optionalPost.get().getProjectId());
                postUiDto.setLikesNumber(optionalPost.get().getLikesNumber());

                newsFeedPosts.add(postUiDto);
            }
        }
        return newsFeedPosts;
    }

    private List<PostUiDto> getNewsFeedFromDb(Long lastPostId){
        //To optimize
        List<UserDto> userDtos = userServiceClient.getFollowees(userContext.getUserId());
        List<Post> posts = postRepository.findByAuthorIds(
                userDtos.stream().map(UserDto::getId).toList(),
                lastPostId,
                feedPageSize
        );
        log.info("getNewsFeedFromDb(): found posts: {}", posts.stream().map(postMapper::toDto).toList());

        List<PostUiDto> postUiDtos = new ArrayList<>();
        for(Post post : posts) {
            for(UserDto userDto : userDtos) {
                if(Objects.equals(userDto.getId(), post.getAuthorId())) {
                    PostUiDto postUiDto = postUiDtoMapper.toDto(post,userCashDtoMapper.toDto(userDto, 0L));
                    postUiDtos.add(postUiDto);
                }
            }
        }
        return postUiDtos;
    }
}
