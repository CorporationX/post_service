package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;
    @PostMapping("/create")
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        commentValidator.validatorCommentDto(commentDto);
        return commentService.createComment(commentDto);
    }

    @PutMapping("/update")
    public void updateComment(@RequestBody CommentDto commentDto) {
        commentValidator.validatorCommentDto(commentDto);
        commentService.updateComment(commentDto);
    }

    @GetMapping("/{postId}/post")
    public List<CommentDto> getAllComments(@PathVariable long postId) {
        commentValidator.validatorId(postId);
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("{/commentId}/comment/{authorId}/author")
    public void deleteComment(@PathVariable long commentId, @PathVariable long authorId) {
        commentValidator.validatorId(commentId);
        commentValidator.validatorId(commentId);
        commentService.deleteComment(commentId, authorId);
    }
}
