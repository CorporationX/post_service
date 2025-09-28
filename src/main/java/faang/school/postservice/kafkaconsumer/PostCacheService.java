package faang.school.postservice.kafkaconsumer;

public interface PostCacheService {
    void addView(long postId);
}
