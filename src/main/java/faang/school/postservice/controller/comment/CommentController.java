package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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
    public CommentDto createComment(@PathVariable("postId") @Positive long postId, @Valid @RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.createComment(postId, createCommentRequest);
    }

    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable("postId") @Positive long postId, @PathVariable @Min(0) long commentId,
                                    @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(postId, commentId, updateCommentRequest);
    }

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable("postId") @Positive long postId) {
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("commentId") @Positive long commentId) {
        commentService.deleteComment(commentId);
    }
}
