package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable Long postId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}
