package faang.school.postservice.component;

import faang.school.postservice.dto.feed.CommentAddedEvent;
import faang.school.postservice.dto.feed.PostFollowersEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.FeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRepositoryCoordinator {

    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final FeedRedisRepository feedRedisRepository;

    public void addPostToCache(PostRedisDto postDto) {
        postRedisRepository.savePost(postDto);
    }

    public void addCommentOnCache(CommentAddedEvent event) {
        postRedisRepository.addComment(event);
    }

    public void addLikeOnCache(Long postId) {
        postRedisRepository.incrementLikes(postId);
    }

    public void removeLikeOnCache(Long postId) {
        postRedisRepository.decrementLikes(postId);
    }

    public void addPostViewOnCache(Long postId) {
        postRedisRepository.incrementViews(postId);
    }

    public void addAuthorToCache(UserDto userDto) {
        userRedisRepository.saveUser(userDto);
    }

    public void addPostsForFollowersOnCache(PostFollowersEvent event) {
        feedRedisRepository.addPostsForFollowersToFeed(event);
    }
}
