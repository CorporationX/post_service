package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

	@Spy
	@InjectMocks
	private FeedServiceImpl feedService;
	@Mock
	private RedisTemplate<String, Object> redisTemplate;
	@Mock
	private ListOperations<String, Object> listOperations;
	@Mock
	private ValueOperations<String, Object> valueOperations;
	@Mock
	private PostRepository postRepository;
	@Mock
	private SubscriptionRepository subscriptionRepository;
	@Mock
	private UserServiceClient userServiceClient;
	@Mock
	private UserContext userContext;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(feedService, "maxFeedSize", 500);
		ReflectionTestUtils.setField(feedService, "feedCollection", "feed");
		ReflectionTestUtils.setField(feedService, "feedTtl", 86400L);
	}

	@Test
	void addPostToFeed_success() {
		Long subscriberId = 101L;
		Long postId = 1001L;
		String feedKey = "feed:101";

		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(listOperations.remove(feedKey, 0, postId)).thenReturn(1L);

		feedService.addPostToFeed(subscriberId, postId);

		verify(listOperations).remove(feedKey, 0, postId);
		verify(listOperations).leftPush(feedKey, postId);
		verify(redisTemplate).expire(eq(feedKey), any(Duration.class));
		verify(listOperations).trim(feedKey, 0, 499);
	}

	@Test
	void addPostToFeed_nullSubscriberId() {
		Long postId = 1001L;

		feedService.addPostToFeed(null, postId);

		verify(redisTemplate, never()).opsForList();
	}

	@Test
	void addPostToFeed_nullPostId() {
		Long subscriberId = 101L;

		feedService.addPostToFeed(subscriberId, null);

		verify(redisTemplate, never()).opsForList();
	}

	@Test
	void getFeed_successFromRedis() {
		String feedKey = "feed:1";
		when(userContext.getUserId()).thenReturn(1L);
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		when(redisTemplate.opsForList().range(feedKey, 0, 19))
				.thenReturn(Arrays.asList(101L, 102L));

		Post post1 = Post.builder().id(101L).content("Test 1").authorId(2L).publishedAt(LocalDateTime.now()).build();
		Post post2 = Post.builder().id(102L).content("Test 2").authorId(3L).publishedAt(LocalDateTime.now()).build();
		when(postRepository.findById(101L)).thenReturn(Optional.of(post1));
		when(postRepository.findById(102L)).thenReturn(Optional.of(post2));

		UserDto user1 = UserDto.builder().id(2L).username("user1").build();
		UserDto user2 = UserDto.builder().id(3L).username("user2").build();
		when(userServiceClient.getUser(2L)).thenReturn(ResponseEntity.ok(user1));
		when(userServiceClient.getUser(3L)).thenReturn(ResponseEntity.ok(user2));

		List<FeedPostDto> result = feedService.getFeed(null, 20);

		assertEquals(2, result.size());
		assertEquals(101L, result.get(0).id());
		assertEquals("user1", result.get(0).username());
		assertEquals(102L, result.get(1).id());
		assertEquals("user2", result.get(1).username());
	}

	@Test
	void getFeed_fromDatabaseWhenRedisEmpty() {
		String feedKey = "feed:1";
		when(redisTemplate.opsForList()).thenReturn(listOperations);
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);
		when(userContext.getUserId()).thenReturn(1L);

		when(redisTemplate.opsForList().range(feedKey, 0, 19)).thenReturn(Collections.emptyList());
		when(subscriptionRepository.findFollowerIdsByFolloweeId(1L)).thenReturn(Arrays.asList(2L, 3L));

		Post post1 = Post.builder().id(101L).content("Test 1").authorId(2L).publishedAt(LocalDateTime.now()).build();
		Post post2 = Post.builder().id(102L).content("Test 2").authorId(3L).publishedAt(LocalDateTime.now()).build();
		when(postRepository.findPublishedPostsByAuthors(anyList(), anyInt()))
				.thenReturn(Arrays.asList(post1, post2));

		UserDto user1 = UserDto.builder().id(2L).username("user1").build();
		UserDto user2 = UserDto.builder().id(3L).username("user2").build();
		when(userServiceClient.getUser(2L)).thenReturn(ResponseEntity.ok(user1));
		when(userServiceClient.getUser(3L)).thenReturn(ResponseEntity.ok(user2));

		List<FeedPostDto> result = feedService.getFeed(null, 20);

		assertEquals(2, result.size());
		assertEquals(101L, result.get(0).id());
		assertEquals("user1", result.get(0).username());
	}
}