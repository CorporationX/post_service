package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validation.comment.CommentValidator;
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
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    private CommentDto addNewComment(@PathVariable long postId, @RequestBody CommentDto comment) {
        return commentService.addNewComment(postId, comment);
    }

    @PutMapping()
    private CommentDto updateComment(@RequestBody CommentDto comment) {
        return commentService.updateComment(comment);
    }

    @GetMapping("/{postId}")
    private List<CommentDto> getAllComments(@PathVariable long postId) {
        return commentService.getAllComments(postId);
    }


    @DeleteMapping("/{commentId}")
    private CommentDto deleteComment(@PathVariable long commentId) {
        return commentService.deleteComment(commentId);
    }

}
