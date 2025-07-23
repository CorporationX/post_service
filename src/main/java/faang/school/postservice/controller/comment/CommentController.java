package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "Comments", description = "Managing comments related to posts")
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @Operation(
            summary = "Create a comment for a post",
            description = "Creates a new comment for the specified post ID"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable @Positive Long postId,
                             @RequestBody @Valid SaveCommentDto saveCommentDto) {
        return commentService.create(postId, userContext.getUserId(), saveCommentDto);
    }

    @Operation(
            summary = "Update a comment",
            description = "Updates the text of a comment by ID. Only the author of the comment can update it"
    )
    @PutMapping("/{commentId}")
    public CommentDto update(@PathVariable @Positive Long postId,
                             @PathVariable @Positive Long commentId,
                             @RequestBody @Valid SaveCommentDto saveCommentDto) {
        return commentService.update(postId, commentId, userContext.getUserId(), saveCommentDto);
    }

    @Operation(
            summary = "Get all comments for a post",
            description = "Returns a list of all comments for the specified post, sorted by creation date"
    )
    @GetMapping
    public List<CommentDto> getByPostId(@PathVariable @Positive Long postId) {
        return commentService.getByPostId(postId);
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment by ID. Only the author of the comment can delete it"
    )
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable @Positive Long postId,
                       @PathVariable @Positive Long commentId) {
        commentService.delete(postId, commentId, userContext.getUserId());
    }
}
