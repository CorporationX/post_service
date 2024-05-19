package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentValidator commentValidator;

    @PostMapping("/createComment")
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        commentValidator.createCommentController(commentDto.getContent(), commentDto.getAuthorId(), commentDto.getPostId());
        return commentService.createComment(commentDto);
    }

    @PutMapping("/changeComment")
    public CommentDto changeComment(@RequestBody CommentDto commentDto) {
        commentValidator.changeCommentController(commentDto.getId(), commentDto.getContent());
        return commentService.changeComment(commentDto);
    }

    @GetMapping("/getAllCommentsOnPostId")
    public List<CommentDto> getAllCommentsOnPostId(@RequestBody CommentDto commentDto) {
        commentValidator.getAllCommentsOnPostIdController(commentDto.getPostId());
        return commentService.getAllCommentsOnPostId(commentDto);
    }

    @DeleteMapping("/deleteComment")
    public void deleteComment(@RequestBody CommentDto commentDto) {
        commentValidator.deleteCommentController(commentDto.getId());
        commentService.deleteComment(commentDto);
    }
}
