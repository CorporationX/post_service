package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private static final String MESSAGE_COMMENT_IS_NULL = "Comment is null";
    private static final String MESSAGE_INVALID_COMMENT_ID = "Invalid commentId";
    private static final String MESSAGE_INVALID_POST_ID = "Invalid postId";

    private final CommentService service;

    @PostMapping("/post/{postId}")
    public CommentDto addComment(@PathVariable Long postId, @RequestBody CommentDto dto) {
        validateId(postId, MESSAGE_INVALID_POST_ID);
        validateDto(dto);
        return service.addComment(postId, dto);
    }

    @PutMapping("/post/{postId}")
    public CommentDto changeComment(@PathVariable Long postId, @RequestBody CommentDto dto) {
        validateId(postId, MESSAGE_INVALID_POST_ID);
        validateDto(dto);
        return service.changeComment(postId, dto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getAllCommentsOfPost(@PathVariable Long postId) {
        validateId(postId, MESSAGE_INVALID_POST_ID);
        return service.getAllCommentsOfPost(postId);
    }

    @DeleteMapping("/post/{postId}")
    public CommentDto deleteComment(@PathVariable Long postId, @RequestParam Long commentId) {
        validateId(postId, MESSAGE_INVALID_POST_ID);
        validateId(commentId, MESSAGE_INVALID_COMMENT_ID);
        return service.deleteComment(postId, commentId);
    }

    private static void validateId(Long id, String message) {
        if (id < 0) {
            throw new RuntimeException(message);
        }
    }

    private static void validateDto(CommentDto dto) {
        if (dto == null) {
            throw new RuntimeException(MESSAGE_COMMENT_IS_NULL);
        }
    }
}
