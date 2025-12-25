package faang.school.postservice.service.posts;

import faang.school.postservice.dto.post.PostToFeedEvent;

public interface FeedService {
    void addPostToFeeds(PostToFeedEvent event);
}
