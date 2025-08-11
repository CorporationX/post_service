package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * CommentController — REST-контроллер для управления комментариями к постам.
 * <p>
 * Обеспечивает обработку HTTP-запросов для создания, обновления, получения и удаления комментариев.
 * Все операции связаны с конкретными постами и требуют идентификаторов поста и комментария,
 * где это применимо.
 * <p>
 * Методы контроллера делегируют бизнес-логику соответствующему CommentService.
 * Валидация данных и проверка авторизации (например, авторство комментария) предполагается реализованной на уровне сервиса.
 * <p>
 * Путь базового URL — /comments.
 * <p>
 * Использует стандартные HTTP методы:
 * - POST для создания комментария к посту,
 * - PATCH для частичного обновления текста комментария,
 * - GET для получения списка комментариев по id поста,
 * - DELETE для удаления комментария по его id.
 *
 * @author agent
 * @since 10.08.2025
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentViewDto> createComment(@PathVariable Long postId,
                                                        @Valid @RequestBody CommentCreateDto dto) {
        return ResponseEntity.ok(service.create(postId, dto));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentViewDto> updateComment(@PathVariable Long commentId,
                                                        @Valid @RequestBody CommentUpdateDto dto) {
        return ResponseEntity.ok(service.update(commentId, dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getAllComments(@PathVariable Long postId) {
        return ResponseEntity.ok(service.getAllByPostId(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        service.delete(commentId);
        return ResponseEntity.ok().build();
    }
}