package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/createComment/{postId}")
    CommentDto createComment(@PathVariable long postId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping("/updateComment")
    CommentDto updateComment(@Valid @RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/getAllComments/{postId}")
    List<CommentDto> getAllComments(@PathVariable long postId) {
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/deleteComment")
    void deleteComment(@Valid @RequestBody CommentDto commentDto) {
        commentService.deleteComment(commentDto);
    }
}
