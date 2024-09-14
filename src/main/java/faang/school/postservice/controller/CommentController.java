package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<Void> addComment(@RequestBody @Validated CommentDto comment) {
        log.info("Add comment {}", comment);
        commentService.addComment(comment);
        log.info("Added comment {}", comment);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateContent(
            @PathVariable long commentId,
            @RequestParam @Max(4096) String content) {
        log.info("Update content for comment id {}", commentId);
        commentService.updateContent(commentId, content);
        log.info("Updated content for comment id {}", commentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable long postId) {
        log.info("Get comments by post id {}", postId);
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        log.info("Delete comment {}", commentId);
        commentService.deleteComment(commentId);
        log.info("Deleted comment {}", commentId);
        return ResponseEntity.ok().build();
    }
}
