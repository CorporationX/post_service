package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.request.RequestCreateComment;
import faang.school.postservice.dto.comment.request.RequestUpdateComment;
import faang.school.postservice.dto.comment.response.ResponseComment;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "User Subscription Controller", description = "API endpoints for managing user subscriptions")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    @Operation(summary = "Create a new comment",
            description = "Creates a new comment for the post with the specified postId")
    @ApiResponse(responseCode = "201", description = "New comment successfully created",
            content = @Content(schema = @Schema(implementation = ResponseComment.class)))
    public ResponseEntity<ResponseComment> createComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment DTO", required = true) @Valid @RequestBody RequestCreateComment comment) {
        ResponseComment createdComment = commentService.createComment(comment, postId);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping("/post/{postId}")
    @Operation(summary = "Get all comments for a post",
            description = "Returns a list of all comments for the post with the specified postId")
    @ApiResponse(responseCode = "200", description = "List of comments successfully retrieved",
            content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ResponseComment>> getAllCommentsByPostId(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId) {
        List<ResponseComment> comments = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/post/{postId}/{id}")
    @Operation(summary = "Update a comment", description = "Updates an existing comment with the specified id")
    @ApiResponse(responseCode = "200", description = "Comment successfully updated",
            content = @Content(schema = @Schema(implementation = ResponseComment.class)))
    public ResponseEntity<ResponseComment> updateComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment ID", required = true) @PathVariable Long id,
            @Parameter(description = "Comment DTO", required = true) @Valid @RequestBody RequestUpdateComment comment) {
        ResponseComment updatedComment = commentService.updateComment(postId, id, comment);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/post/{postId}/{id}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment with the specified id")
    @ApiResponse(responseCode = "204", description = "Comment successfully deleted")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment ID", required = true) @PathVariable Long id) {
        commentService.deleteComment(postId, id);
        return ResponseEntity.noContent().build();
    }
}