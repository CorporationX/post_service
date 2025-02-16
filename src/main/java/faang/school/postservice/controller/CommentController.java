package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RequestMapping("api/v1/comment")
@RequiredArgsConstructor
@RestController
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;


    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable
                                                                @NotNull
                                                                @Positive(message = "postId should be positive") Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(commentMapper.toDtoList(comments));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDto> createComment(@RequestBody @Valid CommentDto commentDto,
                                                    @PathVariable @NotNull
                                                    @Positive(message = "postId should be positive") Long postId,
                                                    @RequestHeader("x-user-id") @NotNull
                                                    @Positive(message = "userId should be positive") Long userId) {
        Comment comment = commentMapper.toEntity(commentDto);
        Comment result = commentService.createComment(comment, postId, userId);
        return ResponseEntity.ok(commentMapper.toDto(result));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@RequestBody @Valid CommentDto commentDto,
                                                    @PathVariable @NotNull
                                                    @Positive(message = "commentId should be positive") Long commentId,
                                                    @RequestHeader("x-user-id") @NotNull
                                                    @Positive(message = "userId should be positive") Long userId) {
        Comment comment = commentMapper.toEntity(commentDto);
        Comment updated = commentService.updateComment(commentId, comment, userId);
        return ResponseEntity.ok(commentMapper.toDto(updated));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable
                                                 @NotNull
                                                 @Positive(message = "commentId should be positive") Long commentId,
                                                 @RequestHeader("x-user-id") @NotNull
                                                 @Positive(message = "userId should be positive") Long userId) {
        Comment deleted = commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(commentMapper.toDto(deleted));
    }
}
