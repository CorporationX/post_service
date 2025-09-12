package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;

import java.util.List;

/**
 * Сервис для работы с лайками постов и комментариев.
 * <p>
 * Предоставляет методы для получения информации о пользователях,
 * поставивших лайки на посты и комментарии. Обрабатывает валидацию
 * входных параметров и ошибки при работе с данными.
 * </p>
 *
 * @author bozya
 * @since 12.08.2025
 */
public interface LikeService {
    /**
     * Возвращает список пользователей, лайкнувших пост.
     *
     * @param postId ID поста, не может быть null
     * @return список пользователей или пустой список, если лайков нет
     * @throws DataValidationException если postId равен null
     */
    List<UserDto> getUsersWhoLikedPost(Long postId);

    /**
     * Возвращает список пользователей, лайкнувших комментарий.
     *
     * @param commentId ID комментария, не может быть null
     * @return список пользователей или пустой список, если лайков нет
     * @throws DataValidationException если commentId равен null
     * @throws EntityNotFoundException если комментарий не найден
     */
    List<UserDto> getUsersWhoLikedComment(Long commentId);
}