package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "Комментарии", description = "Управление комментариями к постам")
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @Operation(
            summary = "Создать комментарий к посту",
            description = "Создаёт новый комментарий к посту по ID"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable @NotNull @Positive Long postId,
                             @RequestBody @Valid SaveCommentDto saveCommentDto) {
        return commentService.create(postId, userContext.getUserId(), saveCommentDto);
    }

    @Operation(
            summary = "Обновить комментарий",
            description = "Обновляет текст комментария по ID. Только автор комментария может его редактировать"
    )
    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable @NotNull @Positive Long postId,
                             @PathVariable @NotNull @Positive Long commentId,
                             @RequestBody @Valid SaveCommentDto saveCommentDto) {
        return commentService.update(postId, commentId, userContext.getUserId(), saveCommentDto);
    }

    @Operation(
            summary = "Получить комментарии по посту",
            description = "Возвращает список всех комментариев, отсортированных по дате"
    )
    @GetMapping
    public List<CommentDto> getByPostId(@PathVariable @NotNull @Positive Long postId) {
        return commentService.getByPostId(postId);
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет комментарий по ID"
    )
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable @NotNull @Positive Long postId,
                       @PathVariable @NotNull @Positive Long commentId) {
        commentService.delete(postId, commentId, userContext.getUserId());
    }
}
