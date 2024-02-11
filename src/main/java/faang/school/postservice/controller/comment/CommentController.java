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


    @PostMapping("/add/{id}")
    private CommentDto addNewComment(@PathVariable Long id, @RequestBody CommentDto comment) {
        commentValidator.validateCommentData(comment);
        return commentService.addNewComment(id, comment);
    }

    @PutMapping("/change")
    private CommentDto changeComment(@RequestBody CommentDto comment) {
        commentValidator.validateCommentData(comment);
        return commentService.changeComment(comment);
    }

    @GetMapping("/getAll/{id}")
    private List<CommentDto> getAllComments(@PathVariable Long id) {
        return commentService.getAllComments(id);
    }


    @PostMapping("/delete")
    private CommentDto deleteComment(@RequestBody CommentDto comment) {
        return commentService.deleteComment(comment);
    }

}
