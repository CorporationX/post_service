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

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto likePost(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Post currentPost = postService.getPost(likeDto.getPostId());
        boolean ifLikeExistsYet = currentPost.getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(currentUserId));

        if(ifLikeExistsYet){
            likeRepository.deleteByPostIdAndUserId(likeDto.getPostId(), currentUserId);
            return likeDto;
        } else {
            Like newLike = likeMapper.dtoToLike(likeDto);
            newLike.setUserId(currentUserId);
            return likeMapper.likeToDto(likeRepository.save(newLike));
        }
    }

    @Transactional
    public void removeLikeFromPost(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Post currentPost = postService.getPost(likeDto.getPostId());
        boolean ifLikeDoesNotExistYet = currentPost.getLikes().stream()
                .noneMatch(like -> like.getUserId().equals(currentUserId));

        if(ifLikeDoesNotExistYet) {
            Like newLike = likeMapper.dtoToLike(likeDto);
            likeRepository.save(newLike);
        } else {
            likeRepository.deleteByPostIdAndUserId(likeDto.getPostId(), currentUserId);
        }
    }

    @Transactional
    public LikeDto likeComment(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Comment currentComment = commentService.getCommentById(likeDto.getCommentId());
        boolean ifLikeExistsYet = currentComment.getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(currentUserId));

        if(ifLikeExistsYet){
            removeLikeFromComment(likeDto, currentUserId);
            return likeDto;
        } else {
            Like newLike = likeMapper.dtoToEntity(likeDto);
            newLike.setUserId(currentUserId);
            return likeMapper.entityToDto(likeRepository.save(newLike));
        }

    }

    @Transactional
    public void removeLikeFromComment(LikeDto likeDto, Long currentUserId){
        checkIfUserExists(currentUserId);
        Comment currentComment = commentService.getCommentById(likeDto.getCommentId());
        boolean ifLikeDoesNotExistYet = currentComment.getLikes().stream()
                .noneMatch(like -> like.getUserId().equals(currentUserId));

        if(ifLikeDoesNotExistYet){
            likeComment(likeDto, currentUserId);
        } else {
            likeRepository.deleteByCommentIdAndUserId(likeDto.getCommentId(), currentUserId);
        }
    }

    private void checkIfUserExists(Long currentUserId){
        userServiceClient.getUser(currentUserId);
    }
}
