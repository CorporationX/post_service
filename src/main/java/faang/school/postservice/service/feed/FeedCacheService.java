package faang.school.postservice.service.feed;

public interface FeedCacheService {
    void warmUpCacheForUser(Long userId);
}
