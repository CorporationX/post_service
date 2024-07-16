package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto){
        return commentService.createComment(commentDto);
    }

    @PutMapping
    public CommentDto updateComment(@Valid @RequestBody CommentDto commentDto){
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@Positive @PathVariable long id){
        commentService.deleteComment(id);
    }

    @GetMapping("/posts/{id}")
    public void findAllByPostId(@Positive @PathVariable long id){
        commentService.findAllByPostId(id);
    }
}
