package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedPostDto;

import java.util.List;

public interface FeedService {

	void addPostToFeed(Long subscriberId, Long postId);

	List<FeedPostDto> getFeed(Long afterPostId, int limit);
}
