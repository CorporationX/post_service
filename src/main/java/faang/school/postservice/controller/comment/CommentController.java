package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentValidator commentValidator;


    @PostMapping("/post/{id}")
    private CommentDto addNewComment(@PathVariable Long id, @RequestBody CommentDto comment) {
        return commentService.addNewComment(id, comment);
    }

    @PutMapping()
    private CommentDto updateComment(@RequestBody CommentDto comment) {
        return commentService.changeComment(comment);
    }

    @GetMapping("/{postId}")
    private List<CommentDto> getAllComments(@PathVariable Long postId) {
        return commentService.getAllComments(postId);
    }


    @DeleteMapping("/{commentId}")
    private CommentDto deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

}
