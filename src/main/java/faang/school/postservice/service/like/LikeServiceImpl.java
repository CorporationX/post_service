package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LikeServiceImpl implements LikeService {
    LikeRepository likeRepository;
    CommentRepository commentRepository;
    UserServiceClient serviceClient;

    @Override
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        checkPostIdNotNull(postId);

        List<Like> likes = likeRepository.findByPostId(postId);

        return likes.stream()
                .map(Like::getUserId)
                .map(serviceClient::getUser)
                .toList();
    }

    @Override
    public List<UserDto> getUsersWhoLikedComment(Long commentId) {
        checkCommentIdNotNull(commentId);

        List<Like> likes = likeRepository.findByCommentId(
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Комментария с id " + commentId + " не существует")));

        return likes.stream()
                .map(Like::getUserId)
                .map(serviceClient::getUser)
                .toList();
    }

    private void checkPostIdNotNull(Long postId) {
        if (postId == null) {
            throw new DataValidationException("Id поста не может быть null");
        }
    }

    private void checkCommentIdNotNull(Long commentId) {
        if (commentId == null) {
            throw new DataValidationException("Id комментария не может быть null");
        }
    }
}