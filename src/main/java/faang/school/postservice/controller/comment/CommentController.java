package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping("/post/{postId}")
    private CommentDto addNewComment(@PathVariable long postId, @RequestBody CommentDto commentDto) {
        commentValidator.validateComment(commentDto);
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping()
    private CommentDto updateComment(@RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{commentId}")
    private void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/{postId}")
    private List<CommentDto> getAllComments(@PathVariable long postId) {
        return commentService.getAllComments(postId);
    }
}
