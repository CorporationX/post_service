package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        commentValidator.createCommentController(commentDto.getContent(), commentDto.getAuthorId(), commentDto.getPostId());
        return commentService.createComment(commentDto);
    }

    @PutMapping("/change")
    public CommentDto changeComment(@RequestBody CommentDto commentDto) {
        commentValidator.changeCommentController(commentDto.getId(), commentDto.getContent());
        return commentService.changeComment(commentDto);
    }

    @GetMapping("/post/{id}")
    public List<CommentDto> getAllCommentsOnPostId(@RequestParam long id) {
        commentValidator.getAllCommentsOnPostIdController(id);
        return commentService.getAllCommentsOnPostId(id);
    }

    @DeleteMapping("/deleteComment/{id}")
    public void deleteComment(@RequestParam long id) {
        commentValidator.deleteCommentController(id);
        commentService.deleteComment(id);
    }
}
