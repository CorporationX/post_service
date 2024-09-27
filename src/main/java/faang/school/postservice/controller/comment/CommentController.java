package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/post/{postId}/comment")
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable @Min(0) long postId, @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.createComment(postId, createCommentRequest);
    }

    @PutMapping("/commentId")
    public CommentDto updateComment(@PathVariable @Min(0) long postId, @PathVariable @Min(0) long commentId,
                                    @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(postId, commentId, updateCommentRequest);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable @Min(0) long postId) {
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @Min(0) long commentId) {
        commentService.deleteComment(commentId);
    }
}
