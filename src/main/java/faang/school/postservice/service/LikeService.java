package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeServiceValidator validator;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;
    private final LikeMapper likeMapper;

    public void addLikeToPost(LikeDto likeDto) {
        Post post = postService.getPost(likeDto.getPostId());
        UserDto userDto = getUser(likeDto.getUserId());
        Like like = likeMapper.toLike(likeDto);

        Optional<Like> optionalLike = likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId());
        validator.validDuplicateLike(optionalLike);

        post.getLikes().add(like);
        likeRepository.save(like);
    }

    public void deleteLikeFromPost(long postId, long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Like was not found using the passed identifier"));
        Post post = postService.getPost(postId);

        post.getLikes().remove(like.getId());
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public void addLikeToComment(LikeDto likeDto) {
        Comment comment = commentService.getComment(likeDto.getCommentId());
        UserDto user = getUser(likeDto.getUserId());
        Like like = likeMapper.toLike(likeDto);

        Optional<Like> optionalLike = likeRepository.findByCommentIdAndUserId(comment.getId(), user.getId());
        validator.validDuplicateLike(optionalLike);

        comment.getLikes().add(like);
        likeRepository.save(like);
    }

    public void deleteLikeFromComment(long commentId, long userId) {
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Like was not found using the passed identifier"));
        Comment comment = commentService.getComment(commentId);

        comment.getLikes().remove(like.getId());
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private UserDto getUser(long userId) {
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new IllegalArgumentException("User with id not found");
        }
    }
}
