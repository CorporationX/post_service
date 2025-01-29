package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse create(@Valid @RequestBody CreateCommentRequest createRequest) {
        return commentService.create(createRequest);
    }

    @PutMapping
    public CommentResponse update(@Valid @RequestBody CommentUpdateRequest updateRequest) {
        return commentService.update(updateRequest);
    }

    @GetMapping("/{postId}")
    public List<CommentResponse> getAllByPostId(@Valid @NotNull @Positive @PathVariable Long postId) {
        return commentService.getAllByPostId(postId);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@Valid @NotNull @Positive @PathVariable Long commentId) {
        commentService.delete(commentId);
    }
}
