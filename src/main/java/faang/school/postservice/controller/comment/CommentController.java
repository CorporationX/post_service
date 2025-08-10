package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentViewDto> createComment(@PathVariable Long postId, CommentCreateDto dto) {
        return ResponseEntity.ok(commentService.create(postId, dto));
    }

    @PatchMapping("/{postId}/{commentId}")
    public ResponseEntity<CommentViewDto> updateComment(@PathVariable Long postId, @PathVariable Long commentId, CommentUpdateDto dto) {
        return ResponseEntity.ok(commentService.update(postId, commentId, dto));
    }

    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getAllComments(Long postId) {
        return ResponseEntity.ok(commentService.getAllByPostId(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        return ResponseEntity.noContent().build();
    }
}