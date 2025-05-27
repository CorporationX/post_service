package faang.school.postservice.service.feed;

import java.util.List;

public interface FeedManagementService {
    void addPostToFeeds(Long postId, Long timestamp, List<Long> subscriberIds);
    void rebuildFeed(Long userId, List<Long> postIds);
}
