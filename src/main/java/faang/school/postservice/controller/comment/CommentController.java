package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Comment controller", description = "Контроллер для управления комментариями")
public interface CommentController {

    @Operation(
            summary = "Создания комментария",
            description = "Сохраняет комментарий в базе"
    )
    @ApiResponse(responseCode = "200", description = "Комментарий создан")
    @PostMapping("/post/{postId}")
    ResponseEntity<CommentDtoResponse> createComment(@PathVariable long postId,
                                                     @RequestParam String content);

    @Operation(
            summary = "Обновления комментария",
            description = "Обновляет комментарий в базе"

    )
    @ApiResponse(responseCode = "200", description = "Комментарий обновлен")
    @PatchMapping("/{commentId}")
    ResponseEntity<CommentDtoResponse> updateComment(@PathVariable long commentId,
                                                     @RequestParam String newContent);

    @Operation(
            summary = "Получить все комментарии поста",
            description = "Возвращает список комментариев поста из базы"
    )
    @GetMapping("/{postId}")
    ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId);

    @Operation(
            summary = "Удалить комментарий",
            description = "Удаляет из базы"
    )
    @ApiResponse(responseCode = "200", description = "Комментарий удален")
    @DeleteMapping("/{commentId}")
    ResponseEntity<Long> deleteComment(@PathVariable long commentId);

    @ApiResponse(responseCode = "200", description = "Комментарий удален")
    @PostMapping("/uploadFiles")
    ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile files);
}
