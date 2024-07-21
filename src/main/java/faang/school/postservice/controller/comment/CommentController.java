package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService service;


    @PostMapping("/comment")
    public CommentDto addComment(@RequestParam Long postId, @RequestBody CommentDto commentDto) {
        validatePostId(postId);
        validateCommentDto(commentDto);
        return service.addComment(postId, commentDto);
    }

    @PutMapping("/comment")
    public CommentDto updateComment(@RequestParam Long postId, @RequestBody CommentDto commentDto) {
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

    @DeleteMapping("/comment")
    public CommentDto deleteComment(@RequestParam Long postId, @RequestBody CommentDto commentDto) {
        validatePostId(postId);
        validateCommentDto(commentDto);
        return service.deleteComment(postId, commentDto);
    }

    private void validatePostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException(CommentControllerErrors.POST_ID_NULL.value);
        }

        if (postId == 0) {
            throw new IllegalArgumentException(CommentControllerErrors.POST_ID_ZERO.value);
        }
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new IllegalArgumentException(CommentControllerErrors.COMMENT_DTO_NULL.value);
        }
    }
}
