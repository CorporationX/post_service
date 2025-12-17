package faang.school.postservice.service.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ListOperations<String, Object> listOperations;

	@InjectMocks
	private FeedService feedService;

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
}