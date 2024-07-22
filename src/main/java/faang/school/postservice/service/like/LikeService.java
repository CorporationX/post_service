package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.validation.LikeServiceValidator;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeServiceValidator likeServiceValidator;
    private final LikeMapper likeMapper;

    @Transactional
    public LikeDto likePost(Long postId, Long userId) {
        Post post = postService.findById(postId).orElseThrow(() -> {
            log.info("Post with id {} does not exist", postId);
            return new EntityNotFoundException("Post with id " + postId + " does not exist");
        });
        likeServiceValidator.validateByUserAndPostLikePossibility(post, userId);
        Like like = likeRepository.save(Like.builder()
                .post(post)
                .userId(userId)
                .build());
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public LikeDto likeComment(Long commentId, Long userId) {
        Comment comment = commentService.findById(commentId).orElseThrow(() -> {
            log.info("Comment with id {} does not exist", commentId);
            return new EntityNotFoundException("Comment with id " + commentId + " does not exist");
        });
        likeServiceValidator.validateByUserAndCommentLikePossibility(comment, userId);
        Like like = likeRepository.save(Like.builder()
                .comment(comment)
                .userId(userId)
                .build());
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikeComment(Long commentId, Long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
