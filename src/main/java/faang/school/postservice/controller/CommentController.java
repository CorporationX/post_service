package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@RequestBody CommentDto commentDto) {
        return commentService.create(commentDto);
    }

    @PutMapping("/{postId}")
    public CommentDto update(@RequestBody CommentDto commentDto, @PathVariable long postId) {
        return commentService.update(commentDto, postId);
    }

    @DeleteMapping("/{postId}")
    public CommentDto delete(@RequestBody CommentDto commentDto, @PathVariable long postId) {
        return commentService.delete(commentDto, postId);
    }

    @GetMapping("/{postId}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable long postId) {
        return commentService.getAllCommentsByPostId(postId);
    }
}
