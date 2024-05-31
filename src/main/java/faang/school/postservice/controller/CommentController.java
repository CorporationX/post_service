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
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/createComment/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(@Min(1) @PathVariable long postId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping("/updateComment")
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/getAllComments/{postId}")
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getAllComments(@Min(1) @PathVariable long postId) {
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/deleteComment")
    @ResponseStatus(HttpStatus.OK)
    void deleteComment(@Valid @RequestBody CommentDto commentDto) {
        commentService.deleteComment(commentDto);
    }
}
