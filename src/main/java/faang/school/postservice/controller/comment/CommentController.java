package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("add")
    public ResponseEntity<CommentDto> addComment(@Validated @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(commentDto);
        return ResponseEntity.ok(createdComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable long commentId) {
        if (commentService.deleteComment(commentId)) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(false);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Boolean> updateComment(@PathVariable long commentId, @Validated @RequestParam String content) {
        commentService.updateComment(commentId, content);
        return ResponseEntity.ok(true);
    }

    @GetMapping("post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
