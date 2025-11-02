package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/save")
    public CommentDto save(@RequestBody @Valid CommentDto commentDto) {
        return commentService.save(commentDto);
    }

    @GetMapping("/all")
    public List<CommentDto> findAllByPostId(@RequestParam long postId) {
        return commentService.findAllByPostId(postId);
    }

    @GetMapping("/{id}")
    public CommentDto findById(@PathVariable long id) {
        return commentService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable long id) {
        commentService.deleteById(id);
    }
}
