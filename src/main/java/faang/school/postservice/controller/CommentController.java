package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody CommentDto commentDto){
        return commentService.createComment(commentDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@Valid @RequestBody CommentDto commentDto){
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(@Positive @PathVariable long id){
        commentService.deleteComment(id);
    }

    @GetMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void findAllByPostId(@Positive @PathVariable long id){
        commentService.findAllByPostId(id);
    }
}
