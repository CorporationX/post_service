package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.PostValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для управления постами.
 * <p>
 * Предоставляет API для выполнения операций с постами, таких как:
 * <ul>
 *     <li>{@link #createDraft(PostCreateDto)}: Создание черновика поста.</li>
 *     <li>{@link #publishPost(Long)}: Публикация поста.</li>
 *     <li>{@link #updatePost(PostUpdateDto, Long)}: Обновление поста.</li>
 *     <li>{@link #softDeletePost(Long)}: Мягкое удаление поста.</li>
 *     <li>{@link #getPost(Long)}: Получение поста по его ID.</li>
 *     <li>{@link #getUserDrafts(Long)}: Получение черновиков постов для пользователя.</li>
 *     <li>{@link #getProjectDrafts(Long)}: Получение черновиков постов для проекта.</li>
 *     <li>{@link #getAuthorPublishedPosts(Long)}: Получение опубликованных постов для пользователя.</li>
 *     <li>{@link #getProjectPublishedPosts(Long)}: Получение опубликованных постов для проекта.</li>
 * </ul>
 * <p>
 * Все методы контроллера используют DTO (Data Transfer Objects) для передачи данных между клиентом и сервером.
 * Валидация входных данных выполняется с использованием аннотаций {@link Valid} и кастомного валидатора {@link PostValidator}.
 * <p>
 * Логирование операций выполняется с использованием библиотеки Lombok ({@link Slf4j}).
 *
 * @author marsel_mkh
 * @see PostService
 * @see PostValidator
 * @see PostCreateDto
 * @see PostUpdateDto
 * @see PostViewDto
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(
        name = "Post Controller",
        description = "Контроллер для управления постами"
)
public class PostController {
    private final PostService postService;

    @Operation(
            summary = "Создает черновик поста.",
            description = "Параметры:postCreateDto DTO с данными для создания поста. " +
                    "Возвращает ResponseEntity с созданным постом в формате PostViewDto."
    )
    @PostMapping
    public ResponseEntity<PostViewDto> createDraft(@Valid @RequestBody PostCreateDto postCreateDto) {
        log.info("Received request to create a draft post: {}", postCreateDto);

        PostViewDto postViewDto = postService.createDraft(postCreateDto);

        log.info("Draft post created successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    @Operation(
            summary = "Публикует пост по его ID.",
            description = "Параметры: postId ID поста для публикации." +
                    "Возвращает ResponseEntity с опубликованным постом в формате PostViewDto"
    )
    @PutMapping("/{postId}/publish")
    public ResponseEntity<PostViewDto> publishPost(
            @PathVariable @Parameter(description = "id поста", required = true, example = "1") Long postId) {
        log.info("Received request to publish post with ID: {}", postId);

        PostViewDto postViewDto = postService.publishPost(postId);

        log.info("Post published successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    @Operation(
            summary = "Обновляет пост по его ID.",
            description = "Параметры: postUpdateDto DTO с данными для обновления поста." +
                    "postId ID поста для обновления." +
                    "Возвращает: ResponseEntity с обновленным постом в формате PostViewDto."
    )
    @PutMapping("/{postId}/update")
    public ResponseEntity<PostViewDto> updatePost(@Valid @RequestBody PostUpdateDto postUpdateDto,
                                                  @PathVariable @Parameter(description = "id поста", required = true, example = "1")
                                                  Long postId) {
        log.info("Received request to update post with ID: {}", postId);

        PostViewDto postViewDto = postService.updatePost(postUpdateDto, postId);

        log.info("Post updated successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    @Operation(
            summary = "Выполняет мягкое удаление поста по его ID.",
            description = "Параметры: postId ID поста для мягкого удаления." +
                    "Возвращает: ResponseEntity с мягко удаленным постом в формате PostViewDto."
    )
    @PutMapping("/{postId}/soft-delete")
    public ResponseEntity<PostViewDto> softDeletePost(
            @PathVariable @Parameter(description = "id поста", required = true, example = "1") Long postId) {
        log.info("Received request to soft delete post with ID: {}", postId);

        PostViewDto postViewDto = postService.softDeletePost(postId);

        log.info("Post soft-deleted successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    @Operation(
            summary = "Получает пост по его ID.",
            description = "Параметры: postId ID поста для получения." +
                    "Возвращает: ResponseEntity с найденным постом в формате PostViewDto."
    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostViewDto> getPost(
            @PathVariable @Parameter(description = "id поста", required = true, example = "1") Long postId) {
        log.info("Received request to get post with ID: {}", postId);

        PostViewDto postViewDto = postService.getPost(postId);

        log.info("Post get successfully: {}", postViewDto);
        return ResponseEntity.ok(postViewDto);
    }

    @Operation(
            summary = "Получает черновики постов для указанного пользователя.",
            description = "Параметры: userId ID пользователя, для которого запрашиваются черновики." +
                    "Возвращает: ResponseEntity со списком черновиков в формате PostViewDto."
    )
    @GetMapping("/user/{userId}/draft")
    public ResponseEntity<List<PostViewDto>> getUserDrafts(
            @PathVariable @Parameter(description = "id пользователя", required = true, example = "1") Long userId) {
        log.info("Received request to get user draft post with ID: {}", userId);

        List<PostViewDto> postsViewDto = postService.getUserDrafts(userId);

        log.info("Draft posts fetched successfully for user with ID: {}." +
                " Number of posts: {}", userId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    @Operation(
            summary = "Получает черновики постов для указанного проекта.",
            description = "Параметры: projectId ID проекта, для которого запрашиваются черновики." +
                    "Возвращает: ResponseEntity со списком черновиков в формате PostViewDto."
    )
    @GetMapping("/project/{projectId}/draft")
    public ResponseEntity<List<PostViewDto>> getProjectDrafts(
            @PathVariable @Parameter(description = "id проекта", required = true, example = "1") Long projectId) {
        log.info("Received request to fetch draft posts for project with ID: {}", projectId);

        List<PostViewDto> postsViewDto = postService.getProjectDrafts(projectId);

        log.info("Draft posts fetched successfully for project with ID:" +
                " {}. Number of posts: {}", projectId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    @Operation(
            summary = "Получает опубликованные посты для указанного пользователя.",
            description = "Параметры: userId ID пользователя, для которого запрашиваются опубликованные посты." +
                    "Возвращает: ResponseEntity со списком опубликованных постов в формате PostViewDto."
    )
    @GetMapping("/user/{userId}/published-post")
    public ResponseEntity<List<PostViewDto>> getAuthorPublishedPosts(
            @PathVariable @Parameter(description = "id пользователя", required = true, example = "1") Long userId) {
        log.info("Received request to fetch published posts for user with ID: {}", userId);

        List<PostViewDto> postsViewDto = postService.getAuthorPublishedPosts(userId);

        log.info("Published posts fetched successfully for user with ID: {}." +
                " Number of posts: {}", userId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }

    @Operation(
            summary = "Получает опубликованные посты для указанного проекта.",
            description = "Параметры: projectId ID проекта, для которого запрашиваются опубликованные посты." +
                    "Возвращает: ResponseEntity со списком опубликованных постов в формате PostViewDto."
    )
    @GetMapping("/project/{projectId}/published-post")
    public ResponseEntity<List<PostViewDto>> getProjectPublishedPosts(
            @PathVariable @Parameter(description = "id проекта", required = true, example = "1") Long projectId) {
        log.info("Received request to fetch published posts for project with ID: {}", projectId);

        List<PostViewDto> postsViewDto = postService.getProjectPublishedPosts(projectId);

        log.info("Published posts fetched successfully for project with ID: {}." +
                " Number of posts: {}", projectId, postsViewDto.size());
        return ResponseEntity.ok(postsViewDto);
    }
}
