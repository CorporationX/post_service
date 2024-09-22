package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;

    public LikeDto addLikeToPost(Long postId, LikeDto likeDto) {
        log.info("Попытка поставить лайк на пост с ID: {}", postId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        if (likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isPresent()) {
            log.warn("Лайк уже поставлен пользователем с ID: {} на пост с ID: {}", likeDto.getUserId(), postId);
            throw new RuntimeException("Лайк уже поставлен");
        }
        likeDto.setPostId(postId);
        Like like = likeMapper.toEntity(likeDto);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);
        log.info("Лайк успешно поставлен пользователем с ID: {} на пост с ID: {}", likeDto.getUserId(), postId);
        return likeMapper.toDto(like);
    }

    public LikeDto removeLikeFromPost(Long postId, LikeDto likeDto) {
        log.info("Попытка удалить лайк на пост с ID: {}", postId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
        log.info("Лайк успешно удалён пользователем с ID: {} на пост с ID: {}", likeDto.getUserId(), postId);
        return likeMapper.toDto(null);
    }

    public LikeDto addLikeToComment(Long commentId, LikeDto likeDto) {
        log.info("Попытка поставить лайк на комментарий с ID: {}", commentId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()).isPresent()) {
            log.warn("Лайк уже поставлен пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
            throw new RuntimeException("Лайк уже поставлен");
        }
        likeDto.setCommentId(commentId);
        Like like = likeMapper.toEntity(likeDto);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);
        log.info("Лайк успешно поставлен пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
        return likeMapper.toDto(like);
    }

    public LikeDto removeLikeFromComment(Long commentId, LikeDto likeDto) {
        log.info("Попытка удалить лайк на комментарий с ID: {}", commentId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
        log.info("Лайк успешно удалён пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
        return likeMapper.toDto(null);
    }

    public List<Long> getLikesFromPost(Long postId) {
        log.info("Запрос на получение лайков для поста с ID: {}", postId);
        List<Long> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
        log.info("Найдено {} лайков для поста с ID: {}", userIds.size(), postId);
        return userIds;
    }

    public List<Long> getLikesFromComment(Long commentId) {
        log.info("Запрос на получение лайков для комментария с ID: {}", commentId);
        List<Long> userIds = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
        log.info("Найдено {} лайков для комментария с ID: {}", userIds.size(), commentId);
        return userIds;
    }
}