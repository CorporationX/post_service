package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@PathVariable("postId") Long postId,
                             @RequestBody CommentDto commentDto) {
        validateComment(commentDto);
        return commentService.create(postId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable("commentId") Long commentId,
                             @RequestBody CommentUpdateDto commentUpdateDto) {
        validateCommentUpdate(commentUpdateDto);
        return commentService.update(commentId, commentUpdateDto);
    }

    @GetMapping
    public List<CommentDto> getByPostId(@PathVariable("postId") Long postId) {
        return commentService.getByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }

    private void validateComment(CommentDto commentDto) {
        validateContent(commentDto.getContent());
    }

    private void validateCommentUpdate(CommentUpdateDto commentUpdateDto) {
        validateContent(commentUpdateDto.getContent());
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new DataValidationException("The comment should not be empty");
        }
        if (content.length() > 4096) {
            throw new DataValidationException("The comment should not be larger than 4096 characters");
        }
    }
}
