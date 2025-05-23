package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.model.Post;

import java.util.List;

public interface FeedService {
    void addPostToFeeds(Long postId, Long timestamp, List<Long> subscriberIds);
    List<FeedPostDto> getFeed(Long userId, Long afterPostId, int limit);
    void cachePost(Post post);
    void cacheAuthor(Long authorId);
    void rebuildFeed(Long userId, List<Long> postIds);
}
