package faang.school.postservice.controller.like;

import faang.school.postservice.dto.error.PostServiceErrorResponseDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Like Controller", description = "Like API")
public interface LikeController {

    @Operation(summary = "Add like to the post", description = "Adds like to the post on behalf of the current user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Like was successfully added to post",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LikeResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User has already liked this post",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostServiceErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post or User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostServiceErrorResponseDto.class))
                    )
            })
    ResponseEntity<LikeResponseDto> addLikeToPost(long postId);

    @Operation(summary = "Add like to the comment", description = "Adds like to the comment on behalf of the " +
            "current user",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Like was successfully added to comment",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LikeResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User has already liked this comment",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostServiceErrorResponseDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostServiceErrorResponseDto.class))
                    )
            })
    ResponseEntity<LikeResponseDto> addLikeToComment(long commentId);

    @Operation(summary = "Delete like from post", description = "Deletes the user's like from the post if it exists",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Like was successfully deleted from post"),
                    @ApiResponse(responseCode = "404", description = "Current user has no like on this post")
            })
    ResponseEntity<Void> deleteLikeFromPost(long postId);

    @Operation(summary = "Delete like from comment", description = "Deletes the user's like from the comment if it exists",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Like was successfully deleted from comment"),
                    @ApiResponse(responseCode = "404", description = "Current user has no like on this comment")
            })
    ResponseEntity<Void> deleteLikeFromComment(long commentId);
}
