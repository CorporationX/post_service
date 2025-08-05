package faang.school.postservice.service.like.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.like.kafka.KafkaLikePublisher;
import faang.school.postservice.service.like.redis.RedisLikeCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final RedisLikeCache redisLikeCache;
    private final KafkaLikePublisher kafkaLikePublisher;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void addLike(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new IllegalArgumentException("Лайк под этим комментарием уже оставлен пользователем с id: " + userId + " id комментария: " + commentId);
        }

        log.info("Ищем комментарий с id: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Комментарий с id " + commentId + " не найден"));

        checkUserExists(userId);

        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();
        Like likeResult = likeRepository.save(like);

        kafkaLikePublisher.publishLikeEvent(new LikeEvent("COMMENT_LIKE", userId, null, commentId, LocalDateTime.now()));
        redisLikeCache.incrementLikes("comment_likes:", commentId);

    }

    @Override
    @Transactional
    public void removeLike(Long commentId, Long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isEmpty()) {
            throw new IllegalArgumentException("Лайк под этим комментарием не найден у пользователя с id: " + userId + " id комментария: " + commentId);
        }

        checkUserExists(userId);
        checkCommentExists(commentId);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);

        kafkaLikePublisher.publishLikeEvent(new LikeEvent("COMMENT_UNLIKE", userId, null, commentId, LocalDateTime.now()));
        redisLikeCache.decrementLikes("comment_likes:", commentId);

    }

    @Override
    @Cacheable(value = "commentLikes", key = "#commentId")
    public int getLikeCount(Long commentId) {
        return likeRepository.countByCommentId(commentId);
    }

    public void checkUserExists(Long userId) {
        log.info("Проверяем существование пользователя с id: {}", userId);
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    public void checkCommentExists(Long commentId) {
        log.info("Проверяем существование комментария с id: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Комментарий с id " + commentId + " не найден");
        }
    }

}
