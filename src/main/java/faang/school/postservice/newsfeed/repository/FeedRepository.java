package faang.school.postservice.newsfeed.repository;

import faang.school.postservice.newsfeed.dto.FeedDto;

public interface FeedRepository {
    FeedDto getFeedByUserId(Long userId);
    void addPostToFeed(Long userId, Long PostId);
}
