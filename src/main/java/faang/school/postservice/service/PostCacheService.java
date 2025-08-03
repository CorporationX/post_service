package faang.school.postservice.service;

public interface PostCacheService {
    void addView(long postId);

    void addLike(long postId, long userId);
}
