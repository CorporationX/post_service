package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/post/{postId}/comment")
@RequiredArgsConstructor
public class CommentController {

    CommentService commentService;

    @PostMapping
    public CommentDto createComment(@PathVariable long postId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping
    public CommentDto updateComment(@PathVariable long postId, CommentDto commentDto) {
        return commentService.updateComment(postId, commentDto);
    }

    @GetMapping
    public List<CommentDto> getAllComment(@PathVariable long postId) {
        return commentService.getAllComment(postId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }
}
