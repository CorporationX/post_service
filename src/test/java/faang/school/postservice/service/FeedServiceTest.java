package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    private static final Long START_POST_ID = 1L;
    private static final Long SECOND_POST_ID = 2L;
    private static final Long THIRD_POST_ID = 3L;
    private static final Long USER_ID = 1L;
    private static final List<Long> AUTHORS = List.of(2L, 3L, 4L);
    private static final int NEWS_FEED_BATCH_SIZE = 2;

    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private FeedService feedService;

    UserDto userDto;
    PostDto postDto1;
    PostDto postDto2;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .id(USER_ID)
                .followeesIds(AUTHORS)
                .build();
        postDto1 = PostDto.builder()
                .id(START_POST_ID)
                .build();
        postDto2 = PostDto.builder()
                .id(SECOND_POST_ID)
                .build();
    }

    @Test
    public void getNewsFeedWhenUserNotFound() {
        String errMessage = String.format("User ID: %d not found", USER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> feedService.getNewsFeed(START_POST_ID, USER_ID));

        assertThat(exception.getMessage().equals(errMessage));
    }

    @Test
    public void getNewsFeedWhenFeedRedisNull() {
        Post post1 = Post.builder()
                .id(START_POST_ID)
                .build();
        Post post2 = Post.builder()
                .id(SECOND_POST_ID)
                .build();

        List<Post> foundPostsInDb = List.of(post1, post2);
        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        ReflectionTestUtils.setField(feedService, "newsFeedBatchSize", NEWS_FEED_BATCH_SIZE);

        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findByAuthorsAndLimitAndStartFromPostId(AUTHORS, NEWS_FEED_BATCH_SIZE, START_POST_ID)).thenReturn(foundPostsInDb);
        when(postMapper.toListDto(foundPostsInDb)).thenReturn(postDtoList);

        TreeSet<PostDto> actualResult = feedService.getNewsFeed(START_POST_ID, USER_ID);

        assertThat(actualResult.size() == 2);
    }

    @Test
    public void getNewsFeedWhenFeedRedisNotNullAndAllPostsInRedis() {
        TreeSet<Long> POST_REDIS_IDS = new TreeSet<>(Comparator.reverseOrder());
        POST_REDIS_IDS.addAll(List.of(1L, 2L, 3L));
        PostRedis postRedis1 = PostRedis.builder()
                .id(THIRD_POST_ID)
                .build();
        PostRedis postRedis2 = PostRedis.builder()
                .id(SECOND_POST_ID)
                .build();
        List<PostRedis> postRedisList = List.of(postRedis1, postRedis2);
        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        FeedRedis feedRedis = FeedRedis.builder()
                .id(USER_ID)
                .postIds(POST_REDIS_IDS)
                .build();

        ReflectionTestUtils.setField(feedService, "newsFeedBatchSize", NEWS_FEED_BATCH_SIZE);

        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(redisFeedRepository.getById(USER_ID)).thenReturn(feedRedis);
        when(postMapper.fromRedisToListDto(postRedisList)).thenReturn(postDtoList);
        when(redisPostRepository.findById(THIRD_POST_ID)).thenReturn(Optional.of(postRedis1));
        when(redisPostRepository.findById(SECOND_POST_ID)).thenReturn(Optional.of(postRedis2));

        TreeSet<PostDto> actualResult = feedService.getNewsFeed(START_POST_ID, USER_ID);

        assertThat(actualResult.size() == 2);
    }

    @Test
    public void getNewsFeedWhenFeedRedisNotNullAndNotEnoughPostsInRedis() {
        TreeSet<Long> POST_REDIS_IDS = new TreeSet<>(Comparator.reverseOrder());
        POST_REDIS_IDS.add(SECOND_POST_ID);
        PostRedis postRedis2 = PostRedis.builder()
                .id(SECOND_POST_ID)
                .build();
        List<PostRedis> postRedisList = List.of(postRedis2);
        List<PostDto> postDtoList = List.of(postDto1, postDto2);

        FeedRedis feedRedis = FeedRedis.builder()
                .id(USER_ID)
                .postIds(POST_REDIS_IDS)
                .build();

        ReflectionTestUtils.setField(feedService, "newsFeedBatchSize", NEWS_FEED_BATCH_SIZE);

        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(redisFeedRepository.getById(USER_ID)).thenReturn(feedRedis);
        when(postMapper.fromRedisToListDto(postRedisList)).thenReturn(postDtoList);
        when(redisPostRepository.findById(SECOND_POST_ID)).thenReturn(Optional.of(postRedis2));

        TreeSet<PostDto> actualResult = feedService.getNewsFeed(START_POST_ID, USER_ID);

        assertThat(actualResult.size() == 2);
    }
}