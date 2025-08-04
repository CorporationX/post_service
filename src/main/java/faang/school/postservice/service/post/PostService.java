package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;

import java.util.List;

/**
 * Сервис для управления публикациями (постами).
 * <p>
 * Определяет основные операции для работы с постами, включая:
 * <ul>
 *   <li>Создание новых постов</li>
 *   <li>Публикацию существующих постов</li>
 *   <li>Обновление и удаление постов</li>
 *   <li>Получение постов по идентификатору</li>
 *   <li>Фильтрацию и получение списка постов</li>
 * </ul>
 *
 * @author Myrza
 * @since 24.07.2025
 */
public interface PostService {
    PostViewDto create(PostCreateDto createDto);

    void publish(long postId);

    PostViewDto update(long postId, PostUpdateDto updateDto);

    void delete(long postId);

    PostViewDto getById(long postId);

    List<PostViewDto> getList(PostFilterDto filterDto);
}
