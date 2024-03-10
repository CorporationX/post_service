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

    @PostMapping("/{id}")
    public CommentDto create(@RequestBody CommentDto commentDto, @PathVariable long id) {
        return commentService.create(commentDto, id);
    }

    @PutMapping("/{id}")
    public CommentDto update(@RequestBody CommentDto commentDto, @PathVariable long id) {
        return commentService.update(commentDto, id);
    }

    @DeleteMapping("/{id}")
    public CommentDto delete(@RequestBody CommentDto commentDto, @PathVariable long id) {
        return commentService.delete(commentDto, id);
    }

    @GetMapping("/{id}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable long id) {
        return commentService.getAllCommentsByPostId(id);
    }
}
