package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeCommentMapper;
import faang.school.postservice.mapper.like.LikePostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikePostMapper likePostMapper;
    private final LikeCommentMapper likeCommentMapper;
    private final UserServiceClient userServiceClient;

    public LikePostDto likePost(LikePostDto likePostDto){
        Post post = postRepository.findById(likePostDto.postId()).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id:  " + likePostDto.postId()));

        likeRepository.findByPostIdAndUserId(likePostDto.postId(), likePostDto.userId())
                .ifPresent(like -> {
                    throw new IllegalArgumentException("This post was already liked by this user");
                });

        Like like = likePostMapper.toEntity(likePostDto);
        like.setPost(post);

        likeRepository.save(like);

        return likePostMapper.toDto(like);
    }

    public void unlikePost(Long postId, Long userId){
        authorizeUser(userId);

        likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for postId: " + postId + " and userId: " + userId));

        likeRepository.deleteByPostIdAndUserId(postId, userId);

        log.info("Like of post was successfully deleted");
    }

    public LikeCommentDto likeComment(LikeCommentDto likeCommentDto){
        Comment comment = commentRepository.findById(likeCommentDto.commentId()).orElseThrow(
                () -> new EntityNotFoundException("Post not found with id:  " + likeCommentDto.postId())
        );

        likeRepository.findByCommentIdAndUserId(likeCommentDto.commentId(), likeCommentDto.userId())
                .ifPresent(like -> {
                    throw new IllegalArgumentException("This comment was already liked by this user");
                });

        Like like = likeCommentMapper.toEntity(likeCommentDto);
        like.setComment(comment);

        Like createdLike = likeRepository.save(like);

        return LikeCommentDto.builder()
                .id(createdLike.getId())
                .userId(createdLike.getUserId())
                .postId(likeCommentDto.postId())
                .commentId(createdLike.getComment().getId())
                .build();
    }

    public void unlikeComment(Long commentId, Long userId){
        authorizeUser(userId);

        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Like not found for commentId: " + commentId + " and userId: " + userId));

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        log.info("Like of comment was successfully deleted");
    }

    private void authorizeUser(Long userId){
        try{
            UserDto userDto = userServiceClient.getUser(userId);
            if (userDto == null){
                log.error("User not found with id: {}", userId);
                throw new EntityNotFoundException("User not found with id: " + userId);
            }
        } catch (FeignException.NotFound e) {
            log.warn("User not found with ID: {}", userId, e);
            throw new EntityNotFoundException("User not found with id: " + userId, e);
        }
    }
}