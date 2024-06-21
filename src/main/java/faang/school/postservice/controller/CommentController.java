package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{postId}")
    CommentDto createComment(@Min(1) @PathVariable long postId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    CommentDto updateComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{postId}")
    List<CommentDto> getAllComments(@Min(1) @PathVariable long postId) {
        return commentService.getAllComments(postId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    void deleteComment(@Valid @RequestBody CommentDto commentDto) {
        commentService.deleteComment(commentDto);
    }
}
