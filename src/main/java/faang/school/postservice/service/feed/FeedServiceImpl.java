package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final UserContext userContext;
	private final PostRepository postRepository;
	private final SubscriptionRepository subscriptionRepository;
	private final UserServiceClient userServiceClient;

	@Value("${feed.max_size:500}")
	private int maxFeedSize;

	@Value("${feed.collection:feed}")
	private String feedCollection;

	@Value("${feed.ttl:24}")
	private Long feedTtl;

	@Override
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

	@Override
	public List<FeedPostDto> getFeed(Long afterPostId, int limit) {
		Long userId = userContext.getUserId();
		List<Long> postIds = getPostIdsFromFeed(userId, afterPostId, limit);

		if (postIds.isEmpty() && afterPostId == null) {
			return getFeedFromDatabase(userId, limit);
		}

		return buildFeedPostDtos(postIds);
	}

	private List<Long> getPostIdsFromFeed(Long userId, Long afterPostId, int limit) {
		String feedKey = buildFeedKey(userId);

		if (afterPostId == null) {
			List<Object> range = redisTemplate.opsForList().range(feedKey, 0, limit - 1);
			if (range == null) {
				return Collections.emptyList();
			}
			return range.stream()
					.map(id -> (Long) id)
					.toList();
		} else {
			List<Object> feed = redisTemplate.opsForList().range(feedKey, 0, -1);
			if (feed == null) {
				return Collections.emptyList();
			}
			int afterIndex = feed.indexOf(afterPostId);
			if (afterIndex == -1 || afterIndex + 1 >= feed.size()) {
				return Collections.emptyList();
			}

			int start = afterIndex + 1;
			int end = Math.min(start + limit - 1, feed.size() - 1);

			return feed.subList(start, end + 1)
					.stream()
					.map(id -> (Long) id)
					.toList();
		}
	}

	private List<FeedPostDto> getFeedFromDatabase(Long userId, int limit) {
		List<Long> followeeIds = subscriptionRepository.findFollowerIdsByFolloweeId(userId);

		if (followeeIds.isEmpty()) {
			return Collections.emptyList();
		}

		List<Post> posts = postRepository.findPublishedPostsByAuthors(followeeIds, limit);

		return posts.stream()
				.map(this::buildFeedPostDto)
				.filter(Objects::nonNull)
				.toList();
	}

	private List<FeedPostDto> buildFeedPostDtos(List<Long> postIds) {
		List<FeedPostDto> result = new ArrayList<>();

		for (Long postId : postIds) {
			Post post = getPostFromCache(postId);
			if (post == null) {
				Optional<Post> postOpt = postRepository.findById(postId);
				if (postOpt.isPresent()) {
					post = postOpt.get();
					savePostToCache(post);
				} else {
					continue;
				}
			}

			FeedPostDto dto = buildFeedPostDto(post);
			if (dto != null) {
				result.add(dto);
			}
		}

		return result;
	}

	private FeedPostDto buildFeedPostDto(Post post) {
		try {
			UserDto author = getUserFromCache(post.getAuthorId());

			if (author == null) {
				ResponseEntity<UserDto> response = userServiceClient.getUser(post.getAuthorId());
				author = response.getBody();
				if (author != null) {
					saveUserToCache(author);
				}
			}

			return FeedPostDto.builder()
					.id(post.getId())
					.content(post.getContent())
					.authorId(post.getAuthorId())
					.username(author != null ? author.username() : "Unknown")
					.publishedAt(post.getPublishedAt())
					.build();
		} catch (Exception e) {
			log.error("Failed to get user for post {}", post.getId(), e);
			return null;
		}
	}

	private String buildFeedKey(Long subscriberId) {
		return feedCollection + ":" + subscriberId;
	}

	private Post getPostFromCache(Long postId) {
		String postKey = "post:" + postId;
		return (Post) redisTemplate.opsForValue().get(postKey);
	}

	private void savePostToCache(Post post) {
		String postKey = "post:" + post.getId();
		redisTemplate.opsForValue().set(postKey, post, Duration.ofHours(feedTtl));
	}

	private UserDto getUserFromCache(Long userId) {
		String userKey = "user:" + userId;
		return (UserDto) redisTemplate.opsForValue().get(userKey);
	}

	private void saveUserToCache(UserDto user) {
		String userKey = "user:" + user.id();
		redisTemplate.opsForValue().set(userKey, user, Duration.ofHours(feedTtl));
	}
}