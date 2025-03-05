package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseCommentDto addComment(@PathVariable @Min(1) @NotNull Long postId, @RequestBody @Valid CommentDto commentDto) {
        return commentService.addComment(postId, commentDto);
    }

    @PostMapping("/update")
    public ResponseCommentDto updateComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/get/{postId}")
    public List<ResponseCommentDto> getAllCommentsByPostId(@PathVariable @Min(1) @NotNull Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteComment(@PathVariable @Min(1) @NotNull Long id) {
        commentService.deleteComment(id);
    }

    @GetMapping("/get/{postId}/ids")
    public List<Long> getVerifiedCommentIdsByPostId(@PathVariable @Min(1) @NotNull Long postId) {
        return commentService.getCommentIdsByPostId(postId);
    }
}