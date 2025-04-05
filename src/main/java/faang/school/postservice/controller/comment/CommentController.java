package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentFiltersDto;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment Controller", description = "API for managing comments and uploading images")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Create a new comment", description = "Creates a new comment with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public CommentResponseDto createComment(@Valid @RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment", description = "Deletes a comment by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public void deleteComment(
            @Parameter(description = "ID of the comment to delete", required = true)
            @NotNull @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update a comment", description = "Updates an existing comment with new details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public CommentResponseDto updateComment(
            @Parameter(description = "ID of the comment to update", required = true)
            @NotNull @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        return commentService.updateComment(commentId, commentUpdateDto);
    }

    @GetMapping
    @Operation(summary = "Get comments by filters", description = "Retrieves comments based on the provided filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filters provided")
    })
    public List<CommentResponseDto> getCommentsByFilters(@Valid CommentFiltersDto commentFiltersDto) {
        return commentService.getComments(commentFiltersDto);
    }

    @PostMapping("/{commentId}/image")
    @Operation(summary = "Upload an image for a comment", description = "Uploads an image for a specific comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or comment ID provided"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    public void uploadImage(
            @Parameter(description = "ID of the comment to upload the image for", required = true)
            @Valid @Positive @PathVariable Long commentId,
            @Parameter(description = "Image file to upload", required = true)
            @NotEmpty @RequestParam("file") MultipartFile file) {
        commentService.uploadImage(commentId, file);
    }
}
