package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
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
    PostRepository postRepository;
    CommentRepository commentRepository;
    UserServiceClient serviceClient;

    /**
     * Получает список пользователей, поставивших лайк указанному посту.
     *
     * @param postId идентификатор поста (не может быть null)
     * @return список DTO пользователей, поставивших лайк
     * @throws DataValidationException если postId равен null
     * @throws EntityNotFoundException если пост с указанным id не найден
     * @see UserDto
     */
    @Override
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        checkPostIdNotNull(postId);

        List<Like> likes = likeRepository.getLikesByPost(
                postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Поста с id " + postId + " не существует")));

        return likes.stream()
                .map(Like::getUserId)
                .map(serviceClient::getUser)
                .toList();
    }

    /**
     * Получает список пользователей, поставивших лайк указанному комментарию.
     *
     * @param postId идентификатор поста (не может быть null)
     * @param commentId идентификатор комментария (не может быть null)
     * @return список DTO пользователей, поставивших лайк
     * @throws DataValidationException если postId или commentId равны null
     * @throws EntityNotFoundException если пост или комментарий с указанными id не найдены
     * @see UserDto
     */
    @Override
    public List<UserDto> getUsersWhoLikedComment(Long postId, Long commentId) {
        checkPostIdNotNull(postId);
        checkCommentIdNotNull(commentId);

        List<Like> likes = likeRepository.getLikesByPostAndComment(
                postRepository.findById(postId)
                        .orElseThrow(() -> new EntityNotFoundException("Поста с id " + postId + " не существует")),
                commentRepository.findById(commentId)
                        .orElseThrow(() -> new EntityNotFoundException("Комментария с id " + commentId + " не существует")));

        return likes.stream()
                .map(Like::getUserId)
                .map(serviceClient::getUser)
                .toList();
    }

    private void checkPostIdNotNull(Long postId) {
        if(postId == null) {
            throw new DataValidationException("Id поста не может быть null");
        }
    }

    private void checkCommentIdNotNull(Long commentId) {
        if(commentId == null) {
            throw new DataValidationException("Id комментария не может быть null");
        }
    }
}