package faang.school.postservice.service.feed;

public interface FeedService {
    void bindPostToFollower(Long followerId, Long postId);

}
