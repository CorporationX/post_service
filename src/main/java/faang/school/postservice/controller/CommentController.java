package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.ControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/{postId}")
public class CommentController {
    private static final String MESSAGE_INVALID_COMMENT_ID = "Invalid commentId";
    private static final String MESSAGE_INVALID_POST_ID = "Invalid postId";

    private final CommentService service;
    private final ControllerValidator validator;

    @PostMapping("/comment")
    public CommentDto addComment(@PathVariable Long postId, @RequestBody CommentDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateDto(dto);
        return service.addComment(postId, dto);
    }

    @PutMapping("/comment")
    public CommentDto changeComment(@PathVariable Long postId, @RequestBody CommentDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateDto(dto);
        return service.changeComment(postId, dto);
    }

    @GetMapping("/comments")
    public List<CommentDto> getAllCommentsOfPost(@PathVariable Long postId) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        return service.getAllCommentsOfPost(postId);
    }

    @DeleteMapping("/comment")
    public CommentDto deleteComment(@PathVariable Long postId, @RequestParam Long commentId) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateId(commentId, MESSAGE_INVALID_COMMENT_ID);
        return service.deleteComment(postId, commentId);
    }
}
