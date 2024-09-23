package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("{id}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable(name = "id") long postId) {
        return commentService.getAllCommentsByPostId(postId);
    }

    @DeleteMapping("{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }
}
