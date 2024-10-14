package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import faang.school.postservice.repository.feed.RedisPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    public void updateFeed(Long subscriberId, Long postId) {
        redisFeedRepository.addPost(subscriberId, postId);
    }

    public void updateFeed(List<Long> subscribersIds, Long postId) {
        subscribersIds.forEach(userId -> updateFeed(userId, postId));
    }

    public void updatePostComments(Long postId, FeedCommentEvent event) {
        PostDto postDto = redisPostRepository.getPost(postId);

        if (postDto == null) {
            Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post with ID {} not found", postId));

        }
    }
}
