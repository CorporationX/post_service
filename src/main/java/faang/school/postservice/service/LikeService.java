package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;
    private final PostMapper postMapper;

    @Transactional
    public LikeDto likePost(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Post currentPost = postService.getPost(likeDto.getPostId());
        if(currentPost.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))){
            removeLikeFromPost(likeDto, currentUserId);
        }
        currentPost.getLikes().add(likeMapper.dtoToLike(likeDto));
        currentPost = postService.updateLikes(currentPost);
        Like newLike = currentPost.getLikes().stream()
                .filter(like -> like.getId() == likeDto.getId())
                .toList().get(0);
        return likeMapper.likeToDto(likeRepository.findById(newLike.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID %d didn't add to post with ID %d.",
                        newLike.getId(), likeDto.getPostId()))));
    }

    @Transactional
    public LikeDto removeLikeFromPost(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Post currentPost = postService.getPost(likeDto.getPostId());
        if(currentPost.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))) {
            likePost(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        currentPost.getLikes().remove(requiredLike);
        postService.updateLikes(currentPost);
        return likeMapper.likeToDto(likeRepository.findById(requiredLike.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID %d didn't remove form post with ID %d.",
                        requiredLike.getId(), currentPost.getId()))));
    }

    @Transactional
    public LikeDto likeComment(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Comment currentComment = commentService.getCommentById(likeDto.getCommentId());
        if(currentComment.getLikes().stream()
                .map(Like::getUserId)
                .anyMatch(userId -> userId.equals(currentUserId))){
            removeLikeFromComment(likeDto, currentUserId);
        }
        currentComment.getLikes().add(likeMapper.dtoToLike(likeDto));
        commentService.updateLikes(currentComment);
        Like newLike = currentComment.getLikes().stream()
                .filter(like -> like.getId() == likeDto.getId())
                .toList().get(0);
        return likeMapper.likeToDto(likeRepository.findById(newLike.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID %d didn't add to post with ID %d.",
                        newLike.getId(), currentComment.getId()))));
    }

    @Transactional
    public LikeDto removeLikeFromComment(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Comment currentComment = commentService.getCommentById(likeDto.getCommentId());
        if(currentComment.getLikes().stream()
                .map(Like::getUserId)
                .noneMatch(userId -> userId.equals(currentUserId))){
            likeComment(likeDto, currentUserId);
        }
        Like requiredLike = likeRepository.findById(likeDto.getId()).orElseThrow();
        currentComment.getLikes().remove(requiredLike);
        commentService.updateLikes(currentComment);
        return likeMapper.likeToDto(likeRepository.findById(requiredLike.getId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Like with ID %d didn't remove form post with ID %d.",
                        requiredLike.getId(), currentComment.getId()))));
    }

    private void checkIfUserExists(Long currentUserId){
        userServiceClient.getUser(currentUserId);
    }
}
