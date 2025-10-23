package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface PostApi {

    @Operation(
            summary = "Создать черновик поста",
            description = "Создает новый пост в статусе черновика. Пост можно будет отредактировать и опубликовать позже."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Черновик поста успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public PostDto createDraftPost(PostCreateDraftDto postCreateDraftDto);

    @Operation(
            summary = "Опубликовать пост",
            description = "Публикует черновик поста, делая его доступным для просмотра другими пользователями."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно опубликован"),
            @ApiResponse(responseCode = "400", description = "Ты не можешь редактировать данные пост"),
            @ApiResponse(responseCode = "403", description = "Пост уже опубликован или не может быть опубликован"),
            @ApiResponse(responseCode = "404", description = "Пост не найден"),
    })
    public PostDto publishedPost(Long postId);

    @Operation(
            summary = "Обновить пост",
            description = "Обновляет содержимое поста. Можно обновлять только посты в статусе черновика."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пост не найден"),
            @ApiResponse(responseCode = "403", description = "Нельзя обновить опубликованный пост")
    })
    public PostDto updatePost(
            @Parameter(description = "ID поста для обновления", example = "123")
            Long postId,
            PostUpdateDto postUpdateDto);

    @Operation(
            summary = "Удалить пост",
            description = "Удаляет пост по его идентификатору. Можно удалять как черновики, так и опубликованные посты."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    public void deleteById(
            @Parameter(description = "ID поста для удаления", example = "123")
            Long postId);

    @Operation(
            summary = "Получить пост по ID",
            description = "Возвращает полную информацию о посте по его идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пост найден"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    public PostDto getPostById(
            @Parameter(description = "ID поста", example = "123")
            Long postId);

    @Operation(
            summary = "Получить черновики автора",
            description = "Возвращает список всех черновиков постов указанного автора."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список черновиков успешно получен"),
            @ApiResponse(responseCode = "400", description = "Не указан ID автора")
    })
    @GetMapping("/draft")
    public List<PostDto> getDraftPostByAuthorId(
            @Parameter(description = "ID автора", example = "456", required = true)
            Long authorId);

    @Operation(
            summary = "Получить опубликованные посты автора",
            description = "Возвращает список всех опубликованных постов указанного автора."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список опубликованных постов успешно получен"),
            @ApiResponse(responseCode = "400", description = "Не указан ID автора")
    })
    public List<PostDto> getPublishedPostByAuthorId(
            @Parameter(description = "ID автора", example = "456", required = true)
            Long authorId);
}
