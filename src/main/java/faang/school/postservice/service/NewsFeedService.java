package faang.school.postservice.service;

import faang.school.postservice.dto.news.feed.NewsFeed;

import java.util.List;

public interface NewsFeedService {

    NewsFeed getNewsFeedBy(long userId);

    NewsFeed getNewsFeedBy(long userId, long firstPostId);

    List<Long> getPostIdsForNewsFeedBy(long userId, long firstPostId);
}
