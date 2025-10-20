package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.Request.RequestCommentDto;
import faang.school.postservice.dto.comment.Response.ResponseCommentDto;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@Tag(name = "User Subscription Controller", description = "API endpoints for managing user subscriptions")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a new comment",
            description = "Creates a new comment for the post with the specified postId")
    @ApiResponse(responseCode = "201", description = "New comment successfully created",
            content = @Content(schema = @Schema(implementation = ResponseCommentDto.class)))
    public ResponseEntity<ResponseCommentDto> createComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment DTO", required = true) @Valid @RequestBody RequestCommentDto commentDto) {
        ResponseCommentDto createdComment = commentService.createComment(commentDto, postId);
        return ResponseEntity.ok(createdComment);
    }

    @GetMapping
    @Operation(summary = "Get all comments for a post",
            description = "Returns a list of all comments for the post with the specified postId")
    @ApiResponse(responseCode = "200", description = "List of comments successfully retrieved",
            content = @Content(schema = @Schema(implementation = List.class)))
    public ResponseEntity<List<ResponseCommentDto>> getAllCommentsByPostId(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId) {
        List<ResponseCommentDto> comments = commentService.getAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a comment", description = "Updates an existing comment with the specified id")
    @ApiResponse(responseCode = "200", description = "Comment successfully updated",
            content = @Content(schema = @Schema(implementation = ResponseCommentDto.class)))
    public ResponseEntity<ResponseCommentDto> updateComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment ID", required = true) @PathVariable Long id,
            @Parameter(description = "Comment DTO", required = true) @Valid @RequestBody RequestCommentDto commentDto) {
        ResponseCommentDto updatedComment = commentService.updateComment(postId, id, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment with the specified id")
    @ApiResponse(responseCode = "204", description = "Comment successfully deleted")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Post ID", required = true) @PathVariable Long postId,
            @Parameter(description = "Comment ID", required = true) @PathVariable Long id) {
        commentService.deleteComment(postId, id);
        return ResponseEntity.noContent().build();
    }
}