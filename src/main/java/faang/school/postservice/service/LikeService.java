package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.integration.UserService;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.messaging.kafka.publishing.like.LikeProducer;
import faang.school.postservice.messaging.kafka.publishing.like.UnlikeProducer;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.messaging.redis.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;
    private final LikeProducer likeProducer;
    private final UnlikeProducer unlikeProducer;
    private final RedisTemplate<Long, Object> redisCacheTemplate;
    private final RedisPostRepository redisPostRepository;


    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        long postId = likeDto.getPostId();
        Post existingPost = postService.getPostIfExist(postId);

        UserDto userDto = userService.getUser();
        validateUserHasNotLiked(postId, userDto.getId());

        Like like = new Like();
        like.setUserId(userDto.getId());
        like.setPost(existingPost);
        like = likeRepository.save(like);

        likeEventPublisher.publishLikeEvent(like, existingPost);
        likeProducer.publish(parseLikeEvent(like, postId));

        return likeMapper.toDto(like);
    }



    @Transactional
    public LikeDto likeComment(LikeDto likeDto) {
        long commentId = likeDto.getCommentId();
        Comment existingComment = commentService.findExistingComment(commentId);

        UserDto userDto = userService.getUser();
        validateUserHasNotLiked(commentId, userDto.getId());

        Like like = new Like();
        like.setUserId(userDto.getId());
        like.setComment(existingComment);
        Like savedLike = likeRepository.save(like);

        return likeMapper.toDto(savedLike);
    }

    @Transactional
    public void unlikePost(long postId) {
        UserDto userDto = userService.getUser();
        Like like = getExistingLikeForPost(postId, userDto.getId());

        likeRepository.delete(like);
        unlikeProducer.publish(parseLikeEvent(like, postId));
    }

    @Transactional
    public void unlikeComment(long commentId) {
        UserDto userDto = userService.getUser();
        Like like = getExistingLikeForComment(commentId, userDto.getId());
        likeRepository.delete(like);
    }

    public void redisLikeIncrement(long postId) {
        redisCacheTemplate.watch(postId);
        Optional<PostRedisDto> optionalPost = redisPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            while (true) {
                PostRedisDto post = optionalPost.get();
                redisCacheTemplate.multi();
                post.likeIncrement();
                redisPostRepository.save(post);
                List<Object> results = redisCacheTemplate.exec();
                if (results != null && !results.isEmpty()) {
                    break;
                }
            }
        }
    }

    private Like getExistingLikeForComment(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User with userId - " + userId + " hasn't liked this comment"));
    }

    private Like getExistingLikeForPost(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException
                        ("User with userId - " + userId + " hasn't liked this post with id - " + postId));
    }

    private void validateUserHasNotLiked(long id, long userId) {
        boolean isLikedPost = likeRepository.findByPostIdAndUserId(id, userId).isPresent();
        boolean isLikedComment = likeRepository.findByCommentIdAndUserId(id, userId).isPresent();

        if (isLikedPost || isLikedComment) {
            throw new DataValidationException("User has already liked this post/comment");
        }
    }

    private LikeEvent parseLikeEvent(Like like, Long postId) {
        return LikeEvent.builder()
                .id(like.getId())
                .postId(postId)
                .build();
    }
}
