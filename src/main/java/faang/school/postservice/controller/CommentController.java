package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final UserContext userContext;
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@PathVariable @Validated Long postId,
                                 @RequestBody @Validated CreateCommentDto commentDto) {
        return ResponseEntity.ok(commentService.addComment(postId, commentDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@RequestBody @Validated UpdateCommentDto updateDto) {
        return ResponseEntity.ok(commentService.updateComment(userContext.getUserId(), updateDto));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable @Validated Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Validated Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
