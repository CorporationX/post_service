package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.avro.LikeCreateEventAvro;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.AfterCommitManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с лайками постов и комментариев.
 * <p>
 * Предоставляет методы для получения информации о пользователях, поставивших лайки
 * на посты и комментарии. Включает валидацию входных параметров и обработку
 * ошибок при работе с репозиториями.
 * </p>
 *
 * @author bozya
 * @since 12.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient serviceClient;
    private final AfterCommitManager commitManager;
    private final KafkaLikeProducer producer;
    private final LikeMapper mapper;
    private final UserContext userContext;

    @Override
    public List<UserViewDto> getUsersWhoLikedPost(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .filter(Objects::nonNull)
                .toList();

        return serviceClient.getUsersByIds(userIds);
    }

    @Override
    public List<UserViewDto> getUsersWhoLikedComment(Long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .filter(Objects::nonNull)
                .toList();

        return serviceClient.getUsersByIds(userIds);
    }

    @Override
    @Transactional
    public void addLikeToPost(Long postId) {
        Long userId = userContext.getUserId();
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            return;
        }

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.format("Пост с id = %d не был найден", postId));
        }

        Post postFromDb = postRepository.findPostOrThrow(postId);
        Like like = mapper.toEntity(userId, postFromDb);
        Like savedLike = likeRepository.save(like);
        buildAndSendEvent(savedLike);
    }

    @Override
    @Transactional
    public void addLikeToComment(Long commentId) {
        Long userId = userContext.getUserId();
        if (likeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return;
        }

        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format("Комментарий с id = %d не был найден", commentId));
        }

        Comment commentFromDb = commentRepository.findByIdOrThrow(commentId);
        Like like = mapper.toEntity(userId, commentFromDb);
        Like savedLike = likeRepository.save(like);
        buildAndSendEvent(savedLike);
    }

    private void buildAndSendEvent(Like like) {
        LikeCreateEventAvro event = mapper.toAvro(like);
        commitManager.executeAfterCommit(() -> producer.sendMessage(event));
    }
}