package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @PostMapping("/posts/{postId}/comments")
    public CommentDto createComment(@RequestHeader("x-user-id") Long userId,
                                    @PathVariable Long postId,
                                    @RequestBody @Valid CommentDto commentDto) {
        return commentService.createComment(userContext.getUserId(), postId, commentDto);
    }

    @PutMapping("/comments")
    public CommentDto updateComment(@RequestParam("id") Long commentId, @RequestBody @Valid CommentDto commentDto) {
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @DeleteMapping("/comments")
    public void deleteComment(@RequestParam("id") Long commentId) {
        commentService.deleteComment(commentId);
    }
}
