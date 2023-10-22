package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Tag(name="Comment Controller")
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public CommentDto deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }
}
