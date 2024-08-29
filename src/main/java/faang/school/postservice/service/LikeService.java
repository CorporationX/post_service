package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeMessagePublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
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
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeMessagePublisher likeMessagePublisher;

    @Transactional
    public LikeDto likePost(Long postId, Long userId) {
        Post post = postService.getPost(postId);
        likeValidator.validateUserAndPost(post, userId);
        Like like = likeRepository.save(Like.builder()
                .post(post)
                .userId(userId)
                .build());
        sendLikeNotification(like);
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public LikeDto likeComment(Long commentId, Long userId) {
        Comment comment = commentService.findById(commentId);
        likeValidator.validateUserAndComment(comment, userId);
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

    private void sendLikeNotification(Like like) {
        LikeEvent likeEvent = LikeEvent.builder()
                .postId(like.getPost().getId())
                .authorId(like.getPost().getAuthorId())
                .likeAuthorId(like.getUserId())
                .build();
        likeMessagePublisher.publish(likeEvent);
    }
}