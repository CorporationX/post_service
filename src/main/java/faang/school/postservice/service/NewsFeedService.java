package faang.school.postservice.service;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.KafkaProducerConfig;
import faang.school.postservice.config.RedisConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostAndFollowersDto;
import faang.school.postservice.dto.post.PostCashDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUiDto;
import faang.school.postservice.dto.user.UserCashDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostAndFollowersMapper;
import faang.school.postservice.mapper.PostCashDtoMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    @Value("${spring.newsfeed.size:100}")
    private int newsFeedMaxSize;
    @Value("${spring.newsfeed.page.size:20}")
    private int feedPageSize;

    @Value("${spring.newsfeed.warmup.days:1}")
    private int warmupDays;
    @Value("${spring.newsfeed.warmup.post.batch:1000}")
    private int postBatchSize;

    private static final String NEWS_FEED_KEY_PREFIX = "newsfeed:";
    private final RedisTemplate<String, Long> redisNewsFeedTemplate;
    private final PostCashRepository postCashRepository;
    private final UserCashDtoCashRepository userCashDtoRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaProducerConfig kafkaProducerConfig;
    private final KafkaLikeProducerService kafkaLikeProducerService;
    private final UserContext userContext;
    private final PostMapper postMapper;
    private final PostUiDtoMapper postUiDtoMapper;
    private final PostAndFollowersMapper postAndFollowersMapper;
    private final UserCashDtoMapper userCashDtoMapper;
    private final PostCashDtoMapper postCashDtoMapper;
    private final RedisConfig redisConfig;

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
        long start = 0;
        long end = feedPageSize - 1;

        if (lastPostId != null) {
            Long rank = redisNewsFeedTemplate.opsForZSet().rank(key, lastPostId);
            if (rank == null) {
                return getNewsFeedFromDb(lastPostId, feedPageSize);
            }
            start = rank + 1;
            end = start + feedPageSize - 1;
        }
        Set<Long> postIds = redisNewsFeedTemplate.opsForZSet().range(key, start, end);
        TreeSet<Long> reversedPostIdsFromCash = new TreeSet<>(Comparator.reverseOrder());
        reversedPostIdsFromCash.addAll(postIds);
        if (reversedPostIdsFromCash.isEmpty() && lastPostId == null) { // Cash пустой
            return getNewsFeedFromDb(Long.MAX_VALUE, feedPageSize);
        }
        if (reversedPostIdsFromCash.isEmpty()) {                    //lastPostId - последний элемент в Cash
            return getNewsFeedFromDb(lastPostId, feedPageSize);
        }
        if (reversedPostIdsFromCash.size() >= feedPageSize) {       // Cash содержит id всех постов
            return collectPostsAndUsersData(reversedPostIdsFromCash);
        } else {                                                    // Cash частично содержит id запрашиваемых постов
            List<PostUiDto> postUiDtos = new ArrayList<>();
            postUiDtos.addAll(collectPostsAndUsersData(reversedPostIdsFromCash));
            postUiDtos.addAll(getNewsFeedFromDb(reversedPostIdsFromCash.last(),
                    feedPageSize - reversedPostIdsFromCash.size()));
            return postUiDtos;
        }
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

    /**
     * Fills up newsfeed cash with data supposedly after loss data in newsfeed cash
     */
    public void warmup() {
        warmupWithRecentPosts(warmupDays);
    }

    private void warmupWithRecentPosts(int days) {
        log.info("Warmup newsfeed with recent post for last {} days STARTS", days);
        int pageNumber = 0;
        long countPosts = 0;
        Pageable pageable = PageRequest.of(pageNumber, postBatchSize);
        while(true) {
            Page<Post> postPage = postRepository.findRecentPosts(pageable, days);
            pageable = pageable.next();
            List<PostDto> postDtos =  postPage.getContent().stream()
                    .map(postMapper::toDto)
                    .toList();
            publishPostsToCash(postPage.getContent());
            countPosts += postPage.getContent().size();
            if(!postPage.hasNext()) {
                break;
            }
        }
        log.info("Warmup newsfeed with recent posts has FINISHED sending last batch of posts. {} posts processed", countPosts);
    }

    private void publishPostsToCash(List<Post> posts){
        List<Long> userIds = new ArrayList<>(posts.stream()
                .map(Post::getAuthorId)
                .distinct()
                .toList());
        List<Long> notCashedUserIds = getUserIdsThatAreNotInCash(userIds);
        if(!notCashedUserIds.isEmpty()) {
            List<UserDto> userDtos = userServiceClient.getUsersByIds(notCashedUserIds);
            for (UserDto userDto : userDtos) {
                userCashDtoRepository.save(userCashDtoMapper.toUserCashDto(userDto, redisConfig.getUserTtl()));
                log.info(">>> Users added to Cash: {}", userDtos);
            }
        }
        for(Post post : posts) {
            postCashRepository.save(postCashDtoMapper.toDto(post, redisConfig.getPostTtl()));
            kafkaLikeProducerService.send(kafkaProducerConfig.getPostAndFollowersTopicName(), postAndFollowersMapper.toDto(post));
        }
    }

    /**
     * Takes input userIds and removes from it id(s) that are not presented in Cash
     * @param userIds List of user id(s)
     * @return List of user id(s) of UserCashDto(s) that are NOT presented in Cash
     */
    private List<Long> getUserIdsThatAreNotInCash(List<Long> userIds) {
        List<Long> presentedInCashIds = new ArrayList<>();
        for(Long id : userIds) {
            Optional<UserCashDto> optional = userCashDtoRepository.findById(id);
            if(optional.isPresent()){
                presentedInCashIds.add(id);
            }
        }
        log.info(">>> presentedInCashUserIds: {}; all users in cash:{}", presentedInCashIds, userIds.size() == presentedInCashIds.size());
        userIds.removeAll(presentedInCashIds);
        return userIds;
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

    private List<PostUiDto> collectPostsAndUsersData(Set<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        Iterable<PostCashDto> postsFromCash =  postCashRepository.findAllById(postIds);
        Set<Long> existingPostCashIds = StreamSupport.stream(postsFromCash.spliterator(), false)
                .map(PostCashDto::getId)
                .collect(Collectors.toSet());

        List<Long> missingPostCashIds = postIds.stream()
                .filter(id -> !existingPostCashIds.contains(id))
                .toList();

        List<PostUiDto> newsFeedPosts = new ArrayList<>();

        if(!existingPostCashIds.isEmpty()) {
            newsFeedPosts.addAll(collectCashPosts(postsFromCash));
        }

        if(!missingPostCashIds.isEmpty()) {
            newsFeedPosts.addAll(getPostsFromDb(missingPostCashIds));
        }

        newsFeedPosts.sort(Comparator.comparing(PostUiDto::getId).reversed());
        return newsFeedPosts;
    }

    private List<PostUiDto> getNewsFeedFromDb(Long lastPostId, int postsNumber) {
        //To optimize
        List<UserDto> userDtos = userServiceClient.getFollowees(userContext.getUserId());
        List<Post> posts = postRepository.findByAuthorIds(
                userDtos.stream().map(UserDto::getId).toList(),
                lastPostId,
                postsNumber
        );
        log.info("getNewsFeedFromDb(): found posts: {}", posts.stream().map(postMapper::toDto).toList());

        List<PostUiDto> postUiDtos = new ArrayList<>();
        for (Post post : posts) {
            for (UserDto userDto : userDtos) {
                if (Objects.equals(userDto.getId(), post.getAuthorId())) {
                    PostUiDto postUiDto = postUiDtoMapper.toDto(post, userCashDtoMapper.toUserCashDto(userDto, 0L));
                    postUiDtos.add(postUiDto);
                }
            }
        }
        return postUiDtos;
    }

    private List<PostUiDto> collectCashPosts(Iterable<PostCashDto> postCashDtos) {
        List<PostUiDto> newsFeedPosts = new ArrayList<>();
        for(PostCashDto postCashDto : postCashDtos) {
            PostUiDto postUiDto = new PostUiDto();
            postUiDto.setId(postCashDto.getId());
            postUiDto.setContent(postCashDto.getContent());
            postUiDto.setAuthor(
                    userCashDtoRepository.findById(postCashDto.getAuthorId())
                            .map(userCashDtoMapper::toUserDto)
                            .orElseGet(() -> userServiceClient.getUser(postCashDto.getAuthorId()))
            );
            postUiDto.setProjectId(postCashDto.getProjectId());
            postUiDto.setLikesNumber(postCashDto.getLikesNumber());

            newsFeedPosts.add(postUiDto);
            log.info("collectCashPosts(): {}", postUiDto);
        }
        return newsFeedPosts;
    }

    private List<PostUiDto> getPostsFromDb(List<Long> postIds) {
        Iterable<Post> posts = postRepository.findAllById(postIds);
        List<PostUiDto> newsFeedPosts = new ArrayList<>();
        for(Post p : posts) {
            UserDto userDto = userServiceClient.getUser(p.getAuthorId());
            UserCashDto userCashDto = userCashDtoMapper.toUserCashDto(userDto, 0L);
            newsFeedPosts.add(postUiDtoMapper.toDto(p, userCashDto));
            log.info("Post postId={} not found in Cash. Retrieved from DB", p.getId());
        }
        return newsFeedPosts;
    }
}
