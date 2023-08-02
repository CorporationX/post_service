package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    public LikeDto likePost(LikeDto likeDto, Long currentUserId) {
        validateCurrentUserInSystem(currentUserId);
        Post currentPost = validatePost(likeDto.getPostId());
        if (currentPost.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))) {
            removeLikeFromPost(likeDto, currentUserId);
        }
        Like like = likeMapper.dtoToLike(likeDto);
        like.setPost(currentPost);
        Like DBLike = likeRepository.save(like);
        LikeDto resultFromDB = likeMapper.likeToDto(DBLike);
        resultFromDB.setPostId(DBLike.getPost().getId());
        return resultFromDB;
    }

    public LikeDto removeLikeFromPost(LikeDto likeDto, Long currentUserId) {
        validateCurrentUserInSystem(currentUserId);
        Post currentPost = validatePost(likeDto.getPostId());
        if (currentPost.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))) {
            likePost(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        likeRepository.deleteByPostIdAndUserId(currentPost.getId(), currentUserId);
        LikeDto result = likeMapper.likeToDto(requiredLike);
        result.setPostId(currentPost.getId());
        return result;
    }

    public LikeDto likeComment(LikeDto likeDto, Long currentUserId) {
        validateCurrentUserInSystem(currentUserId);
        Comment currentComment = validateComment(likeDto.getCommentId());
        if (currentComment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))) {
            removeLikeFromComment(likeDto, currentUserId);
        }
        Like like = likeMapper.dtoToLike(likeDto);
        like.setComment(currentComment);
        Like DBLike = likeRepository.save(like);
        LikeDto resultFromDB = likeMapper.likeToDto(DBLike);
        resultFromDB.setCommentId(DBLike.getComment().getId());
        return resultFromDB;
    }

    public LikeDto removeLikeFromComment(LikeDto likeDto, Long currentUserId) {
        validateCurrentUserInSystem(currentUserId);
        Comment currentComment = validateComment(likeDto.getCommentId());
        if (currentComment.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))) {
            likeComment(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        likeRepository.deleteByCommentIdAndUserId(currentComment.getId(), currentUserId);
        LikeDto result = likeMapper.likeToDto(requiredLike);
        result.setCommentId(currentComment.getId());
        return result;
    }

    private void validateCurrentUserInSystem(Long currentUserId) {
        userServiceClient.getUser(currentUserId);
    }

    private Post validatePost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new DataValidationException(String.format("The post with ID %d doesn't exist", postId)));
    }

    private Comment validateComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new DataValidationException(String.format("The comment with ID %d doesn't exist", commentId)));
    }
}
