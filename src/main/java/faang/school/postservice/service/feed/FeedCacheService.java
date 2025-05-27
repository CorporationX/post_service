package faang.school.postservice.service.feed;

import faang.school.postservice.model.Post;

public interface FeedCacheService {
    void cachePostAndAuthor(Post post);
}
