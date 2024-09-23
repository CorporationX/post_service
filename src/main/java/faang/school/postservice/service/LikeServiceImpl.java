package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class LikeServiceImpl implements LikeService {
    public final LikeValidator likeValidator;
    public final PostRepository postRepository;
    public final CommentRepository commentRepository;
    public final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public void addLikeToPost(LikeDto likeDto, long postId) {
        Like like = likeMapper.toLike(likeDto);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        likeValidator.validateLike(like, post);
        try {
            userServiceClient.getUser(like.getUserId());
        } catch (FeignException e) {
            throw new DataValidationException("There is no such user");
        }
        likeValidator.validatePostAndCommentLikes(post, like);
        like.setPost(post);
        likeRepository.save(like);
    }

    @Override
    public void deleteLikeFromPost(LikeDto likeDto, long postId) {
        Like like = likeMapper.toLike(likeDto);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There is no such post"));
        if (!post.getLikes().remove(like)) {
            throw new DataValidationException("Post is not liked");
        }
        likeRepository.delete(like);
        postRepository.save(post);
    }

    @Override
    public void addLikeToComment(LikeDto likeDto, long commentId) {
        Like like = likeMapper.toLike(likeDto);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("There is no such comment"));
        checkUser(like.getUserId());
        likeValidator.validateLike(like, comment.getPost());
        likeValidator.validatePostAndCommentLikes(comment.getPost(), like); //возможна рекурсия
        like.setComment(comment);
        likeRepository.save(like);
    }

    @Override
    public void deleteLikeFromComment(LikeDto likeDto, long commentId) {
        Like like = likeMapper.toLike(likeDto);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("There is no such comment"));
        if (!comment.getLikes().remove(like)) {
            throw new DataValidationException("Comment is not liked");
        }
        likeRepository.delete(like);
        commentRepository.save(comment);
    }

    @Override
    public List<LikeDto> findLikesOfPublishedPost(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new DataValidationException("There is no such post"));
        if (post.isPublished()) {
            return postRepository.findById(postId)
                    .orElseThrow(() -> new DataValidationException("There is no such comment"))
                    .getLikes().stream().map(likeMapper::toLikeDto).toList();
        } else {
            throw new DataValidationException("Post is not published");
        }
    }

     private void checkUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataValidationException("There is no such user");
        }
    }
}