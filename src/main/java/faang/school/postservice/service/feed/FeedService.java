package faang.school.postservice.service.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
	private final RedisTemplate<String, Object> redisTemplate;

	@Value("${feed.max_size:500}")
	private int maxFeedSize;

	@Value("${feed.collection:feed}")
	private String feedCollection;

	@Value("${feed.ttl:24}")
	private Long feedTtl;

	public void addPostToFeed(Long subscriberId, Long postId) {
		if (subscriberId == null) {
			log.warn("Attempt to add post {} to feed with null subscriber ID", postId);
			return;
		}

		if (postId == null) {
			log.warn("Attempt to add null post to feed of subscriber {}", subscriberId);
			return;
		}

		String feedKey = buildFeedKey(subscriberId);
		redisTemplate.opsForList().remove(feedKey, 0, postId);
		redisTemplate.opsForList().leftPush(feedKey, postId);
		redisTemplate.expire(feedKey, Duration.ofHours(feedTtl));
		redisTemplate.opsForList().trim(feedKey, 0, maxFeedSize - 1);
	}

	private String buildFeedKey(Long subscriberId) {
		return feedCollection + ":" + subscriberId;
	}
}
