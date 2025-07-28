package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.post.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для управления постами
 * Предоставляет endpoints для создания, обновления, получения списка
 * и получения поста по индентификатору.
 *
 * @author Linempy
 * @since 26.07.2025
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceImpl service;

    /**
     * Создает новый пост.
     *
     * @param createDto DTO с данными для создания поста {@link PostCreateDto}
     * @return DTO созданного поста {@link PostViewDto}
     */
    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostViewDto create(@RequestBody PostCreateDto createDto) {
        return service.create(createDto);
    }

    /**
     * Публикует существующий пост.
     *
     * @param postId ID поста для публикации
     * @return HTTP 204 No Content при успешной публикации
     */
    @PutMapping("/posts/{postId}/publications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> publication(@PathVariable Long postId) {
        service.publication(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновляет существующий пост.
     *
     * @param postId ID поста для обновления
     * @param dto DTO с обновленными данными поста {@link PostUpdateDto}
     * @return DTO обновленного поста {@link PostViewDto}
     */
    @PutMapping("/posts/{postId}")
    public PostViewDto update(@PathVariable Long postId, @RequestBody PostUpdateDto dto) {
        return service.update(postId, dto);
    }

    /**
     * Помечает пост как удаленный (soft delete).
     *
     * @param postId ID поста для удаления
     */
    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> softDelete(@PathVariable Long postId) {
        service.softDelete(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает пост по его ID.
     *
     * @param postId ID поста
     * @return DTO запрошенного поста {@link PostViewDto}
     */
    @GetMapping("/posts/{postId}")
    public PostViewDto getById(@PathVariable Long postId) {
        return service.getById(postId);
    }

    /**
     * Получает черновики пользователя, отсортированные по дате создания (новые сначала).
     *
     * @param userId ID пользователя
     * @return список DTO черновиков {@link PostViewDto}
     */
    @GetMapping("/users/{userId}/posts/drafts")
    public List<PostViewDto> getByUserDraftPostsSortedByCreation(@PathVariable Long userId) {
        return service.getByUserDraftPostsSortedByCreation(userId);
    }

    /**
     * Получает опубликованные посты пользователя, отсортированные по дате публикации (новые сначала).
     *
     * @param userId ID пользователя
     * @return список DTO опубликованных постов {@link PostViewDto}
     */
    @GetMapping("/users/{userId}/posts/published")
    public List<PostViewDto> getByUserPublishedPostsSortedByPublication(@PathVariable Long userId) {
        return service.getByUserPublishedPostsSortedByPublication(userId);
    }

    /**
     * Получает черновики проекта, отсортированные по дате создания (новые сначала).
     *
     * @param projectId ID проекта
     * @return список DTO черновиков {@link PostViewDto}
     */
    @GetMapping("/projects/{projectId}/posts/drafts")
    public List<PostViewDto> getByProjectDraftPostsSortedByCreation(@PathVariable Long projectId) {
        return service.getByProjectDraftPostsSortedByCreation(projectId);
    }

    /**
     * Получает опубликованные посты проекта, отсортированные по дате публикации (новые сначала).
     *
     * @param projectId ID проекта
     * @return список DTO опубликованных постов {@link PostViewDto}
     */
    @GetMapping("/projects/{projectId}/posts/published")
    public List<PostViewDto> getByProjectPublishedPostsSortedByPublication(@PathVariable Long projectId) {
        return service.getByProjectPublishedPostsSortedByPublication(projectId);
    }


}