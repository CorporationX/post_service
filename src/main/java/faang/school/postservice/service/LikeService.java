package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeValidation;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeRepository likeRepository;
    private final LikeValidation likeValidation;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public LikeDto likePost(LikeDto likeDto) {
        Post checkPost = postService.getPost(likeDto.getPostId());
        userServiceClient.getUser(likeDto.getUserId());
        likeValidation.verifyUniquenessLikePost(likeDto.getPostId(), likeDto.getUserId());
        Like like = Like.builder()
                .post(checkPost)
                .id(likeDto.getUserId())
                .build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    public void deleteLikeFromPost(Long postId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByPostIdAndUserId(postId, userDto.getId());
    }

    public LikeDto likeComment(LikeDto likeDto) {
        Comment checkComment = commentService.findCommentById(likeDto.getCommentId());
        userServiceClient.getUser(likeDto.getUserId());
        likeValidation.verifyUniquenessLikeComment(likeDto.getCommentId(), likeDto.getUserId());
        Like like = Like.builder()
                .comment(checkComment)
                .id(likeDto.getUserId())
                .build();
        return likeMapper.toDto(likeRepository.save(like));
    }

    public void deleteLikeFromComment(Long commentId) {
        UserDto userDto = getUserFromUserService();
        likeRepository.deleteByCommentIdAndUserId(commentId, userDto.getId());
    }

    private UserDto getUserFromUserService() {
        try {
            return userServiceClient.getUser(userContext.getUserId());
        } catch (FeignException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}