package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * Сервис для управления постами
 * <p>
 * Предоставляет полный набор операций для работы с постами пользователей и проектов,
 * включая создание, публикацию, редактирование и удаление.
 * </p>
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Создание постов в статусе черновика</li>
 *   <li>Публикация черновиков</li>
 *   <li>Управление видимостью постов (soft delete)</li>
 *   <li>Получение постов с различными фильтрами</li>
 * </ul>
 *
 * @author Linempy
 * @since 25.07.2025
 * @version 1.0
 */
public interface PostService {

    /**
     * Создает новый пост на основе предоставленных данных.
     *
     * @param createDto DTO с данными для создания поста
     * @return DTO созданного поста в статусе черновика
     * @throws jakarta.validation.ValidationException если данные не прошли валидацию
     */
    PostViewDto create(PostCreateDto createDto);

    /**
     * Публикует существующий черновик поста.
     *
     * @param id идентификатор поста для публикации
     */
    void publication(Long id);

    /**
     * Обновляет содержимое существующего поста.
     *
     * @param id идентификатор поста для обновления
     * @param dto DTO с обновленными данными поста
     * @return DTO обновленного поста
     * @throws jakarta.validation.ValidationException если данные не прошли валидацию
     */
    PostViewDto update(Long id, PostUpdateDto dto);

    /**
     * Помечает пост как удаленный (soft delete).
     *
     * @param id идентификатор поста для удаления
     */
    void softDelete(Long id);

    /**
     * Получает пост по его идентификатору.
     *
     * @param id идентификатор запрашиваемого поста
     * @return DTO поста
     */
    PostViewDto getById(Long id);

    /**
     * Получает черновики пользователя, отсортированные по дате создания (новые сначала).
     *
     * @param userId идентификатор пользователя
     * @return список DTO черновиков пользователя
     */
    List<PostViewDto> getByUserDraftPostsSortedByCreation(Long userId);

    /**
     * Получает опубликованные посты пользователя, отсортированные по дате публикации (новые сначала).
     *
     * @param userId идентификатор пользователя
     * @return список DTO опубликованных постов пользователя
     */
    List<PostViewDto> getByUserPublishedPostsSortedByPublication(Long userId);

    /**
     * Получает черновики проекта, отсортированные по дате создания (новые сначала).
     *
     * @param projectId идентификатор проекта
     * @return список DTO черновиков проекта
     */
    List<PostViewDto> getByProjectDraftPostsSortedByCreation(Long projectId);

    /**
     * Получает опубликованные посты проекта, отсортированные по дате публикации (новые сначала).
     *
     * @param projectId идентификатор проекта
     * @return список DTO опубликованных постов проекта
     */
    List<PostViewDto> getByProjectPublishedPostsSortedByPublication(Long projectId);
}