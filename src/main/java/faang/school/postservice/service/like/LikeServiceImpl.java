package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserContext userContext;
    private final LikeMapper likeMapper;

    @Override
    public LikeDto likePost(Long postId) {
        long userId = userContext.getUserId();
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked post with id %d"
                    .formatted(userId, postId));
        }
        Like like = Like.builder()
                .userId(userId)
                .post(getPostById(postId))
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto removeLikeOnPost(Long postId) {
        long userId = userContext.getUserId();
        Like like = likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on post with id %d does not exist"
                        .formatted(userId, postId)));
        likeRepository.delete(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto likeComment(Long commentId) {
        long userId = userContext.getUserId();
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new UserAlreadyLikedException("User with id %d is already liked comment with id %d"
                    .formatted(userId, commentId));
        }
        Like like = Like.builder()
                .userId(userId)
                .comment(getCommentById(commentId))
                .build();
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    public LikeDto removeLikeOnComment(Long commentId) {
        long userId = userContext.getUserId();
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(() ->
                new EntityNotFoundException("Like by user with id %d on comment with id %d does not exist".formatted(userId, commentId)));
        likeRepository.delete(like);
        return likeMapper.toLikeDto(like);
    }

    private Post getPostById(long postId) {
        return postRepository
                .findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post with id %d does not exist".formatted(postId)));
    }

    private Comment getCommentById(long commentId) {
        return commentRepository
                .findById(commentId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Comment with id %d does not exist".formatted(commentId)));
    }
}
