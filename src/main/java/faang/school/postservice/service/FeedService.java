package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.feed.CommentAddedEvent;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.FeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final UserContext userContext;
    private final FeedRedisRepository feedRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Value("${spring.data.redis.object-cache-options.returning-posts-count}")
    private int returningPostsCount;

    public List<PostResponseDto> getFeed(Long postId) {
        Long userId = userContext.getUserId();
        //TODO: возврат feed, если не найден в Redis, то искать в БД
        return null;
    }

    private UserDto findAuthorByIdOnCache(Long authorId) {
        return userRedisRepository.findById(authorId);
    }

    private List<Long> findFeedByUserIdOnCache(Long userId, int returningPostsCount) {
        return feedRedisRepository.getFeed(userId, returningPostsCount);
    }

    private PostRedisDto findPostByIdOnCache(Long postId) {
        return postRedisRepository.findById(postId);
    }

    private List<CommentAddedEvent> findCommentsByPostIdOnCache(Long postId) {
        return postRedisRepository.getComments(postId);
    }

    private Long findLikesByPostIdOnCache(Long postId) {
        return postRedisRepository.getLikes(postId);
    }

    private Long findViewsByPostIdOnCache(Long postId) {
        return postRedisRepository.getViews(postId);
    }
}
