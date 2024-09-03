package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name = "Комментарии", description = "Контроллер для работы над комментариями")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать комментарий", description = "Введите данные комментария")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий создан", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))}),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Обновить комментарий", description = "Введите данные комментария, чтобы обновить его содержание")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий обновлен", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))}),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public CommentDto updateComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Удалить комментарий", description = "Введите номер комментария, который нужно удалить")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий удален"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")

    })
    public void deleteComment(@Positive @PathVariable long id) {
        commentService.deleteComment(id);
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Найти все комментарии", description = "Введите идентификатор поста, чтобы найти все его комментарии")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные"),
            @ApiResponse(responseCode = "500", description = "Произошла ошибка, не зависящая от вызывающей стороны")
    })
    public void findAllByPostId(@Positive @PathVariable long id) {
        commentService.findAllByPostId(id);
    }
}
