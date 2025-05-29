package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для обработки запросов, связанных с комментариями.
 * Предоставляет REST-эндпоинты для управления комментариями к постам.
 *
 * <p>Доступные методы:</p>
 * <ul>
 *   <li>{@link #createComment(Long, CommentCreateDto)} - Создание нового комментария</li>
 *   <li>{@link #updateComment(Long, Long, CommentCreateDto)} - Обновление существующего комментария</li>
 *   <li>{@link #getCommentsByPostId(Long)} - Получение всех комментариев к посту</li>
 *   <li>{@link #deleteComment(Long, Long)} - Удаление комментария</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
@Tag(name = "Comment Management", description = "Endpoints for managing post comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment for the specified post"
    )
    @PostMapping
    public ResponseEntity<CommentViewDto> createComment(
            @Parameter(description = "ID of the post to comment on", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "Comment data to create", required = true)
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Request to create comment for post with ID: {}", postId);
        CommentViewDto createdComment = commentService.createComment(postId, commentCreateDto);

        return ResponseEntity.ok(createdComment);
    }

    @Operation(
            summary = "Update a comment",
            description = "Updates the text of an existing comment"
    )
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentViewDto> updateComment(
            @Parameter(description = "ID of the post containing the comment", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "ID of the comment to update", required = true, example = "1")
            @PathVariable Long commentId,
            @Parameter(description = "Updated comment data", required = true)
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Request to update comment with ID: {} for post with ID: {}", commentId, postId);
        CommentViewDto updatedComment = commentService.updateComment(postId, commentId, commentCreateDto);

        return ResponseEntity.ok(updatedComment);
    }

    @Operation(
            summary = "Get post comments",
            description = "Returns all comments for the specified post, sorted by creation date (newest first)"
    )
    @GetMapping
    public ResponseEntity<List<CommentViewDto>> getCommentsByPostId(
            @Parameter(description = "ID of the post to get comments for", required = true, example = "1")
            @PathVariable Long postId) {
        log.info("Request to get comments for post with ID: {}", postId);
        List<CommentViewDto> comments = commentService.getCommentsByPostId(postId);

        return ResponseEntity.ok(comments);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes the comment with the specified ID"
    )
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "ID of the post containing the comment", required = true, example = "1")
            @PathVariable Long postId,
            @Parameter(description = "ID of the comment to delete", required = true, example = "1")
            @PathVariable Long commentId) {
        log.info("Request to delete comment with ID: {} for post with ID: {}", commentId, postId);
        commentService.deleteComment(postId, commentId);

        return ResponseEntity.noContent().build();
    }
}