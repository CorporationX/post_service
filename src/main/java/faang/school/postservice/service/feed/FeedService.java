package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.publishable.fornewsfeed.FeedCommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.feed.RedisFeedRepository;
import faang.school.postservice.repository.feed.RedisPostRepository;
import faang.school.postservice.repository.feed.RedisUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;


    public void addPostToFeed(Long subscriberId, Long postId, LocalDateTime publishedAt) {
        redisFeedRepository.addPost(subscriberId, postId, publishedAt);
    }

    public void addPostToFeed(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        subscribersIds.forEach(userId -> addPostToFeed(userId, postId, publishedAt));
    }

    public void addPostToCache(PostDto postDto) {
        redisPostRepository.addNewPost(postDto);
    }

    public void updatePostInCache(PostDto postDto) {
        addPostToCache(postDto);
        redisPostRepository.deleteCommentCounter(postDto.getId());
        redisPostRepository.deleteLikeCounter(postDto.getId());
    }

    public void addNewComment(Long postId, FeedCommentEvent event) {
        updateComment(postId, event);
        redisPostRepository.incrementComment(postId);
    }

    public void updateComment(Long postId, FeedCommentEvent event) {
        CommentDto commentDto = commentMapper.fromFeedCommentEventToDto(event);
        redisPostRepository.addComment(postId, commentDto);
    }

    public void handlePostDeletion(Long postId) {
        redisPostRepository.deletePost(postId);
        redisPostRepository.deleteComments(postId);
        redisPostRepository.deleteCommentCounter(postId);
        redisPostRepository.deleteLikeCounter(postId);
        redisFeedRepository.deletePostFromAllFeeds(postId);
    }

    public void deleteComment(Long postId, Long commentId) {
        redisPostRepository.deleteComment(postId, commentId);
        redisPostRepository.decrementComment(postId);
    }

    public void incrementLike(Long postId) {
        redisPostRepository.incrementLike(postId);
    }

    public void decrementLike(Long postId) {
        redisPostRepository.decrementLike(postId);
    }

    public void addUserToCache(Long authorId) {
        UserDto userDto = userServiceClient.getUser(authorId);
        redisUserRepository.save(userDto);
    }



    public void addPostToCache(Long postId) {
        Optional<Post> optionalPost = getPostFromDB(postId);
        if (optionalPost.isPresent()) {
            PostDto postDto = postMapper.toDto(optionalPost.get());
            redisPostRepository.addNewPost(postDto);
        }
        addPostCommentsFromDB(postId);
    }

    private void addPostCommentsFromDB(Long postId) {

    }

    private void addToCachePostFromDB(Long postId) {
        Optional<Post> optionalPost = getPostFromDB(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            PostDto postDto = postMapper.toDto(post);
            List<Long> subscribersIds = userServiceClient.getFollowerIdsByFolloweeId(post.getAuthorId());
            addPostToCache(postDto);

            List<Comment> comments = optionalPost.get().getComments();
            comments.stream().sorted()
        }
    }

    private Optional<PostDto> getPostFromCache(Long postId) {
        return Optional.of(redisPostRepository.getPost(postId));
    }

    private Optional<Post> getPostFromDB(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));

        if (post.isDeleted()) {
            log.info("Post with ID {} was found in DB but it was deleted", postId);
            handlePostDeletion(postId);
            return Optional.empty();
        }

        return Optional.of(post);
    }
}
