package faang.school.postservice.service;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.integration.UserService;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UserService userService;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeEventPublisher likeEventPublisher;


    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        long postId = likeDto.getPostId();
        Post existingPost = postService.getPostIfExist(postId);

        UserDto userDto = userService.getUser();
        validateUserHasNotLiked(postId, userDto.getId());

        Like like = new Like();
        like.setUserId(userDto.getId());
        like.setPost(existingPost);
        Like savedLike = likeRepository.save(like);

        likeEventPublisher.publishLikeEvent
                (postId, existingPost.getAuthorId(), savedLike.getUserId(), savedLike.getCreatedAt());

        return likeMapper.toDto(savedLike);
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
    }

    @Transactional
    public void unlikeComment(long commentId) {
        UserDto userDto = userService.getUser();
        Like like = getExistingLikeForComment(commentId, userDto.getId());
        likeRepository.delete(like);
    }

    private Like getExistingLikeForComment(long commentId, long userId) {
        return likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User with userId - " + userId + " hasn't liked this comment"));
    }

    private Like getExistingLikeForPost(long postId, long userId) {
        return likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("User with userId - " + userId + " hasn't liked this post"));
    }

    private void validateUserHasNotLiked(long id, long userId) {
        boolean isLikedPost = likeRepository.findByPostIdAndUserId(id, userId).isPresent();
        boolean isLikedComment = likeRepository.findByCommentIdAndUserId(id, userId).isPresent();

        if (isLikedPost || isLikedComment) {
            throw new DataValidationException("User has already liked this post/comment");
        }
    }
}
