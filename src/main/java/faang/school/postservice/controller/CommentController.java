package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {
    private final CommentService commentService;
    private final UserContext userContext;

    @PostMapping
    public CommentDto create(@Valid @RequestBody CommentDto comment) {
        return commentService.create(comment, userContext.getUserId());
    }

    @PutMapping
    public CommentDto update(@Valid @RequestBody CommentDto comment) throws AuthenticationException {
        long userId = userContext.getUserId();
        if (userId != comment.getAuthorId()) {
            throw new AuthenticationException("Only authors of the comment can update it");
        }
        return commentService.update(comment, userId);
    }

    @GetMapping("/{postId}/comments")
    public List<CommentDto> getPostComments(@PathVariable @NotNull long postId) {
        return commentService.getPostComments(postId);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody CommentDto comment) {
        commentService.delete(comment, userContext.getUserId());
    }
}
