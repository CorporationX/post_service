package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.util.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Descriptor;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Tag(name="CommentController",description = "Контроллер создает, обновляет и удаляет комментарии." +
        " Возвращает комментарии под конкретным постом")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(
            summary = "Создание комментария",
            description = "Создает комментарий под постом"
    )
    @Parameter(description = "Принимает комментарий который нужно создать")
    public CommentDto create(@Valid CommentDto commentDto){
        if (commentDto.getId() != null){
            throw new DataValidationException(ErrorMessage.COMMENT_ID_NOT_NULL_ON_CREATION);
        }
        return commentService.create(commentDto);
    }

    @PutMapping
    @Operation(
            summary = "Обновление комментария",
            description = "Изменяет созданный комментарий"
    )
    @Parameter(description = "Принимает комментарий которую нужно изменить в базе данных")
    public CommentDto update(@Valid CommentDto commentDto){
        return commentService.update(commentDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление комментария",
            description = "Удаляет выбранный комментарий"
    )
    @Parameter(description = "Принимает параметр ID какого комментария нужно удалить")
    public void delete(@PathVariable Long id){
        commentService.delete(id);
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "Возвращает список комментариев",
            description = "Возвращает все комментарии под выбранным постом"
    )
    @Parameter(description = "Принимает номер поста с которого нужно вернуть комментарии")
    public List<CommentDto> getCommentsForPost(@PathVariable Long postId){
        return commentService.getCommentsForPost(postId);
    }
}