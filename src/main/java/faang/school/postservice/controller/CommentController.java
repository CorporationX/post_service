package faang.school.postservice.controller;


import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.comment.CommentServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
@Validated
public class CommentController {
    private final CommentServiceImpl commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comments")
    public void createComment(@Valid @RequestBody CreateCommentDto createCommentDto) {
        commentService.createComment(createCommentDto);
    }

    @PutMapping("/comments/{commentId}")
    public void updateComment(@NotNull(message = "Be sure to include the comment id")
                              @Positive(message = "post must be greater than zero")
                              @PathVariable
                              Long commentId,
                              @Valid
                              @RequestBody
                              UpdateCommentDto updateCommentDto
    ) {
        commentService.updateComment(commentId, updateCommentDto);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<ResponseCommentDto> getComments(
            @Positive(message = "post must be greater than zero")
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return commentService.getComments(postId, page, pageSize);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(
            @Positive(message = "comment must be greater than zero")
            @NotNull @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}