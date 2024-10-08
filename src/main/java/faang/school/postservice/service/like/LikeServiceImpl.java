package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.kafka.EventsGenerator;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

@Slf4j
@Service
@Setter
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    @Value("${like-service.batch-size}")
    private int batchSize;
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final LikeEventPublisher likePublisher;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;

    private final EventsGenerator eventsGenerator;

    @Override
    @Transactional
    public void deleteCommentLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long commentId = likeDto.getCommentId();
        Like like = likeMapper.toEntity(likeDto);
        Comment comment = commentMapper.toEntity(commentService.getComment(commentId));
        comment.getLikes().remove(like);
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    @Override
    @Transactional
    public LikeDto addCommentLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long commentId = likeDto.getCommentId();
        likeValidator.validateUserExistence(userId);
        Comment comment = commentMapper.toEntity(commentService.getComment(commentId));
        likeValidator.validateLikeToComment(comment, userId);
        Like like = likeMapper.toEntity(likeDto);
        comment.getLikes().add(like);
        like = likeRepository.save(like);
        publisher(userId, null, commentId, comment.getAuthorId());
        log.info("Like with likeId = {} added on comment with commentId = {} by user with userId = {}",
                like.getId(),
                commentId,
                userId);
        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public LikeDto addPostLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        likeValidator.validateUserExistence(userId);
        Post post = postMapper.toEntity(postService.getPost(postId));
        likeValidator.validateLikeToPost(post, userId);
        Like like = likeMapper.toEntity(likeDto);
        post.getLikes().add(like);
        like = likeRepository.save(like);
        publisher(userId, postId, null, post.getAuthorId());
        log.info("Like with likeId = {} added on post with postId = {} by user with userId = {}",
                like.getId(),
                postId,
                userId);

        eventsGenerator.generateAndSendLikeEvent(postMapper.toDto(post));

        return likeMapper.toDto(like);
    }

    @Override
    @Transactional
    public void deletePostLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Like like = likeMapper.toEntity(likeDto);
        Post post = postMapper.toEntity(postService.getPost(postId));
        post.getLikes().remove(like);
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    private void publisher(Long userId, Long postId, Long commentId, Long authorId) {
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

    public List<UserDto> findUsersByPostId(Long postId) {
        var userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
        checkUserIdListEmpty(userIds, postId);

        return getUsersInBatches(userIds);
    }

    public List<UserDto> findUsersByCommentId(Long commentId) {
        var userIds = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
        checkUserIdListEmpty(userIds, commentId);

        return getUsersInBatches(userIds);
    }

    private List<UserDto> getUsersInBatches(List<Long> userIds) {
        List<UserDto> allUsers = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += batchSize) {
            int end = min(i + batchSize, userIds.size());
            var batch = userIds.subList(i, end);
            var batchUsers = userServiceClient.getUsersByIds(batch);
            allUsers.addAll(batchUsers);
        }
        return allUsers;
    }

    private void checkUserIdListEmpty(List<Long> userIds, Long id) {
        if (userIds.isEmpty()) {
            throw new EntityNotFoundException("No users found for ID " + id);
        }
    }
}