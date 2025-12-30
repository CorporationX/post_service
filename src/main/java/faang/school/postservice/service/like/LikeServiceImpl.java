package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.LikeAddedEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.like.LikeEventProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.client.UserServiceClientAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {

    private final UserContext userContext;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClientAdapter userServiceClientAdapter;
    private final LikeEventProducer likeEventProducer;

    @Override
    @Transactional
    public LikeDto addLikeToPost(long postId) {
        long userId = userContext.getUserId();
        userServiceClientAdapter.getUserById(userId);
        Post currentPost = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Post #%d is not found", postId)));
        Like like = Like.builder()
                .userId(userId)
                .post(currentPost)
                .build();
        like = likeRepository.save(like);
        log.info("User #{} added like to the Post #{}", like.getUserId(), like.getPost().getId());
        likeEventProducer.sendLikeAddedEvent(
                new LikeAddedEvent(
                        like.getId(),
                        like.getUserId(),
                        like.getPost().getId(),
                        like.getCreatedAt()
                ));
        return likeMapper.toLikeDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromPost(long postId) {
        long currentUserId = userContext.getUserId();
        Like currentLike = likeRepository.findByPostIdAndUserId(postId, currentUserId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User #%d did not add Like to Post #%d",
                        currentUserId,
                        postId)));
        likeRepository.deleteByPostIdAndUserId(postId, currentLike.getUserId());
    }

    @Override
    @Transactional
    public LikeDto addLikeToComment(long commentId) {
        long userId = userContext.getUserId();
        userServiceClientAdapter.getUserById(userId);
        Comment currentComment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment #%d is not found", commentId)));
        Like like = Like.builder()
                .userId(userId)
                .comment(currentComment)
                .build();
        like = likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Override
    @Transactional
    public void removeLikeFromComment(long commentId) {
        long currentUserId = userContext.getUserId();
        Like currentLike = likeRepository.findByCommentIdAndUserId(commentId, currentUserId).orElseThrow(() ->
                new EntityNotFoundException(String.format("User #%d did not add Like to Comment #%d",
                        currentUserId,
                        commentId)));
        likeRepository.deleteByCommentIdAndUserId(commentId, currentLike.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LikeDto> getLikesFromPost(long postId) {
        List<Like> likes = likeRepository.findAllByPostId(postId).orElse(Collections.emptyList());
        return likeMapper.toLikeDtos(likes);
    }
}
