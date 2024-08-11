package faang.school.postservice.controller.comment;

import faang.school.postservice.controller.error.CommentControllerErrors;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;

    @PostMapping("/comment")
    public CommentDto addComment(@RequestParam Long postId, @Valid @RequestBody CommentDto commentDto) {
        validatePostId(postId);
        validateCommentDto(commentDto);
        return service.addComment(postId, commentDto);
    }

    @PutMapping("/comment")
    public CommentDto updateComment(@RequestParam Long postId, @Valid @RequestBody CommentDto commentDto) {
        validatePostId(postId);
        validateCommentDto(commentDto);
        return service.updateComment(postId, commentDto);
    }

    @PostMapping("/comments")
    public List<CommentDto> getComments(@RequestParam Long postId) {
        System.out.println(postId);
        validatePostId(postId);
        return service.getComments(postId);
    }
    @GetMapping("/comment")
    public CommentDto get(@RequestParam Long id) {
        return service.getComment(id);
    }

    @DeleteMapping("/comment")
    public CommentDto deleteComment(@RequestParam Long postId, @Valid @RequestBody CommentDto commentDto) {
        validatePostId(postId);
        validateCommentDto(commentDto);
        return service.deleteComment(postId, commentDto);
    }

    private void validatePostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException(CommentControllerErrors.POST_ID_NULL.getValue());
        }

        if (postId == 0) {
            throw new IllegalArgumentException(CommentControllerErrors.POST_ID_ZERO.getValue());
        }
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException(CommentControllerErrors.COMMENT_DTO_NULL.getValue());
        }
    }
}
