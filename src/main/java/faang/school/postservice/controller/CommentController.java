package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@Valid @NotNull @RequestBody CommentDto request) {
        return commentService.createComment(request);
    }

    @PutMapping("/{id}")
    public CommentDto updateCommentContent(@PathVariable long id, @Valid @RequestBody CommentDto request) {
        return commentService.updateCommentContent(id, request);
    }

    @GetMapping("/all")
    public List<CommentDto> getAllComments(
            @RequestParam Long postId,
            @RequestParam(required = false) Long authorId) {
        CommentDto filter = new CommentDto();
        filter.setPostId(postId);
        filter.setAuthorId(authorId);
        return commentService.getAllComments(filter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable @Min(1) long id) {
        commentService.deleteComment(id);
        return ResponseEntity
                .ok("Comment with ID " + id + " has been deleted successfully.");
    }
}
