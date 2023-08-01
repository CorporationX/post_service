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

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    public LikeDto likePost(LikeDto likeDto, Long currentUserId){
        validateCurrentUserInSystem(currentUserId);
        Post currentPost = validatePost(likeDto.getPostId());
        if(currentPost.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))){
            removeLikeFromPost(likeDto, currentUserId);
        }
        currentPost.getLikes().add(likeMapper.dtoToLike(likeDto));
        return likeMapper.likeToDto(postRepository.save(currentPost)
                .getLikes().stream()
                .filter(like -> like.getId() == likeDto.getId())
                .toList().get(0));

    }

    public LikeDto removeLikeFromPost(LikeDto likeDto, Long currentUserId){
        validateCurrentUserInSystem(currentUserId);
        Post currentPost = validatePost(likeDto.getPostId());
        if(currentPost.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))) {
            likePost(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        currentPost.getLikes().remove(requiredLike);
        postRepository.save(currentPost);
        return likeMapper.likeToDto(requiredLike);
    }

    public LikeDto likeComment(LikeDto likeDto, Long currentUserId){
        validateCurrentUserInSystem(currentUserId);
        Comment currentComment = validateComment(likeDto.getCommentId());
        if(currentComment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))){
            removeLikeFromComment(likeDto, currentUserId);
        }
        currentComment.getLikes().add(likeMapper.dtoToLike(likeDto));
        return likeMapper.likeToDto(commentRepository.save(currentComment)
                .getLikes().stream()
                .filter(like -> like.getId() == likeDto.getId())
                .toList().get(0));
    }

    public LikeDto removeLikeFromComment(LikeDto likeDto, Long currentUserId){
        validateCurrentUserInSystem(currentUserId);
        Comment currentComment = validateComment(likeDto.getCommentId());
        if(currentComment.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))){
            likeComment(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        currentComment.getLikes().remove(requiredLike);
        commentRepository.save(currentComment);
        return likeMapper.likeToDto(requiredLike);
    }

    private void validateCurrentUserInSystem(Long currentUserId){
        userServiceClient.getUser(currentUserId);
    }

    private Post validatePost(Long postId){
        return postRepository.findById(postId).orElseThrow(() ->
                new DataValidationException(String.format("The post with ID %d doesn't exist", postId)));
    }

    private Comment validateComment(Long commentId){
        return commentRepository.findById(commentId).orElseThrow(() ->
                new DataValidationException(String.format("The comment with ID %d doesn't exist", commentId)));
    }
}
