package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentApi {

    @Operation(
            summary = "Создать комментарий к посту",
            description = "Создаёт новый комментарий от текущего пользователя к указанному посту."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные параметры запроса"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    CommentDto createComment(CommentCreateDto commentCreateDto);

    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет текст комментария. Разрешено только автору комментария."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно обновлён"),
            @ApiResponse(responseCode = "403", description = "Нельзя изменить чужой комментарий"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    CommentDto updateComment(Long id, CommentUpdateDto commentUpdateDto);

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет комментарий по ID. Разрешено только автору комментария или администратору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно удалён"),
            @ApiResponse(responseCode = "403", description = "Нельзя удалить чужой комментарий"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    void deleteComment(Long id);

    @Operation(
            summary = "Получить комментарий по ID",
            description = "Возвращает комментарий по его идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий найден"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    CommentDto getCommentById(Long id);

    @Operation(
            summary = "Получить комментарии к посту",
            description = "Возвращает список комментариев для указанного поста."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии успешно получены"),
            @ApiResponse(responseCode = "404", description = "Пост не найден")
    })
    PageResponse<CommentDto> getCommentsByPostId(Long postId, Pageable pageable);
}
