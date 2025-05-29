package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Comment controller", description = "Контроллер для управления комментариями")
public interface CommentController {

    @Operation(
            summary = "Создания комментария",
            description = "Сохраняет комментарий в базе"
    )
    @ApiResponse(responseCode = "200", description = "Комментарий создан")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CommentDtoResponse> createComment(@RequestBody CommentCreateDto commentDto);

    @Operation(
            summary = "Обновления комментария",
            description = "Обновляет комментарий в базе"

    )
    @ApiResponse(responseCode = "200", description = "Комментарий обновлен")
    @PutMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CommentDtoResponse> updateComment(@RequestBody CommentUpdateDto commentDto);

    @Operation(
            summary = "Получить все комментарии поста",
            description = "Возвращает список комментариев поста из базы"
    )
    @GetMapping(value = "/{postId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId);

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет из базы"
    )
    @ApiResponse(responseCode = "200", description = "Комментарий удален")
    @DeleteMapping(value = "/{commentId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Long> deleteComment(@PathVariable long commentId);
}
