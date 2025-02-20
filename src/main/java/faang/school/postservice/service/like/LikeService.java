package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.LikeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final UserServiceClient userServiceClient;
    private final LikeValidator likeValidator;

    public void likePost(PostLikeDto postLikeDto) {
        UserDto user = userServiceClient.getUser(postLikeDto.getUserId());
        likeValidator.validateUserExists(user);

        Post post = postService.getPost(postLikeDto.getPostId());
        likeValidator.validatePostExists(post);

        if (likeRepository.findByPostIdAndUserId(postLikeDto.getPostId(),
                postLikeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this post.");
        }

        createAndSaveLike(likeMapper.toLike(postLikeDto), post, null);
    }

    @Transactional
    public void unlikePost(PostLikeDto postLikeDto) {
        UserDto user = userServiceClient.getUser(postLikeDto.getUserId());
        likeValidator.validateUserExists(user);

        Post post = postService.getPost(postLikeDto.getPostId());
        likeValidator.validatePostExists(post);

        likeRepository.deleteByPostIdAndUserId(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    public void likeComment(CommentLikeDto commentLikeDto) {
        UserDto user = userServiceClient.getUser(commentLikeDto.getUserId());
        likeValidator.validateUserExists(user);

        Comment comment = commentService.getComment(commentLikeDto.getCommentId());
        likeValidator.validateCommentExists(comment);

        if (likeRepository.findByCommentIdAndUserId(commentLikeDto.getCommentId(),
                commentLikeDto.getUserId()).isPresent()) {
            throw new DataValidationException("User already liked this comment.");
        }

        createAndSaveLike(likeMapper.toLike(commentLikeDto), null, comment);
    }

    @Transactional
    public void unlikeComment(CommentLikeDto commentLikeDto) {
        UserDto user = userServiceClient.getUser(commentLikeDto.getUserId());
        likeValidator.validateUserExists(user);

        Comment comment = commentService.getComment(commentLikeDto.getCommentId());
        likeValidator.validateCommentExists(comment);

        likeRepository.deleteByCommentIdAndUserId(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    private void createAndSaveLike(Like like, Post post, Comment comment) {
        if (post != null) {
            like.setPost(post);
        }
        if (comment != null) {
            like.setComment(comment);
        }
        likeRepository.save(like);
    }
}