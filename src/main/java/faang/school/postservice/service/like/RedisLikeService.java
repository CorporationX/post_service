package faang.school.postservice.service.like;

public interface RedisLikeService {
    void incrementLikesForPost(long postId);
}
