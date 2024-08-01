package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final LikeEventPublisher likePublisher;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void deleteCommentLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long commentId = likeDto.getCommentId();
        Like like = likeMapper.toEntity(likeDto);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        comment.getLikes().remove(like);
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    @Override
    @Transactional
    public LikeDto addCommentLike(LikeDto likeDto) {
        likeValidator.validateUserExistence(likeDto);
        Long userId = likeDto.getUserId();
        Long commentId = likeDto.getCommentId();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        Like like = likeMapper.toEntity(likeDto);
        comment.getLikes().add(like);
        like = likeRepository.save(like);
        publishLikeEvent(userId, null, commentId, comment.getAuthorId());
        log.info("Like with likeId = {} added on comment with commentId = {} by user with userId = {}",
                like.getId(),
                commentId,
                userId);
        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public LikeDto addPostLike(LikeDto likeDto) {
        likeValidator.validateUserExistence(likeDto);
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post not found"));
        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        like = likeRepository.save(like);
        publishLikeEvent(userId, postId, null, post.getAuthorId());
        log.info("Like with likeId = {} added on post with postId = {} by user with userId = {}",
                like.getId(),
                postId,
                userId);
        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void deletePostLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Like like = likeMapper.toEntity(likeDto);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException("Post not found"));
        post.getLikes().remove(like);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    private void publishLikeEvent(Long userId, Long postId, Long commentId, Long authorId) {
        LikeEvent event = LikeEvent.builder()
                .authorLikeId(userId)
                .commentId(commentId)
                .postId(postId)
                .authorCommentId(commentId != null ? authorId : null)
                .authorPostId(commentId != null ? authorId : null)
                .completedAt(LocalDateTime.now())
                .build();
        likePublisher.publish(event);
    }
}
