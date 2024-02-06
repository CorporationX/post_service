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

    @PostMapping("/create")
    public CommentDto create(@RequestBody CommentDto commentDto) {
        return commentService.create(commentDto);
    }

    @PutMapping("/update/{id}")
    public CommentDto update(@RequestBody CommentDto commentDto, @PathVariable long id) {
        return commentService.update(commentDto, id);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@RequestBody CommentDto commentDto, @PathVariable long id) {
        commentService.delete(commentDto, id);
    }

    @GetMapping("/comments/{id}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable long id) {
        return commentService.getAllCommentsByPostId(id);
    }
}
