package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.service.like.LikeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Post likes",
        description = "Managing post likes"
)
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeServiceImpl likeService;

    @Operation(
            summary = "Like a post",
            description = "Like a post by it's Id",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            description = "Current user id",
                            required = true,
                            example = "1",
                            in = ParameterIn.HEADER
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post has been liked",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LikeDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Incorrect request parameter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{\"message\": \"User has already set a like on this post\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Post doesn't exist",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Post doesn't exist\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PostMapping("/post/{postId}")
    public LikeDto setLikeOnPost(@PathVariable
                                 @Parameter(description = "Post Id to set a like",
                                         example = "1")
                                 @Positive(message = "Post Id can't be negative")
                                 long postId) {
        return likeService.setLikeOnPost(postId);
    }

    @Operation(
            summary = "Remove a like from a post",
            description = "Remove a like from a post by it's Id",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            description = "Current user id",
                            required = true,
                            example = "1",
                            in = ParameterIn.HEADER
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Like has been deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Incorrect request parameter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"User hasn't liked the post\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found - Post doesn't exist",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Post doesn't exist\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @DeleteMapping("/post/{postId}")
    public void unsetLikeOnPost(@PathVariable
                                @Parameter(description = "Post Id to unset a like",
                                        example = "1")
                                @Positive(message = "Post Id can't be negative")
                                long postId) {
        likeService.unsetLikeOnPost(postId);
    }

    @Operation(
            summary = "Like a comment",
            description = "Like a comment by it's Id",
            parameters = {
                    @Parameter(
                            name = "x-user-id",
                            description = "Current User Id",
                            required = true,
                            example = "1",
                            in = ParameterIn.HEADER
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment has been liked",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LikeDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Incorrect request parameter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"User has already liked a post\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Comment doesn't exist",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Comment doesn't exist\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @PostMapping("/comment/{commentId}")
    public LikeDto setLikeOnComment(@PathVariable
                                    @Parameter(description = "Comment Id to set a like",
                                            example = "1")
                                    @Positive(message = "Comment Id can't be negative")
                                    long commentId) {
        return likeService.setLikeOnComment(commentId);
    }

    @Operation(
            summary = "Remove a like from a comment",
            description = "Remove a like from a comment by it's Id",
            parameters = {
                    @Parameter(name = "x-user-id",
                            description = "Current user id",
                            required = true,
                            example = "1",
                            in = ParameterIn.HEADER)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Like has been deleted",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - Incorrect request parameter",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"User hasn't liked the comment\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - Comment doesn't exist",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Comment doesn't exist\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @DeleteMapping("/comment/{commentId}")
    public void unsetLikeOnComment(@PathVariable
                                   @Parameter(description = "Comment Id to unset a like",
                                           example = "1")
                                   @Positive(message = "Comment Id can't be negative")
                                   long commentId) {
        likeService.unsetLikeOnComment(commentId);
    }

    @Operation(
            summary = "Get likes count",
            description = "Get likes count by post id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Likes count by post id",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Integer.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found - post doesn't exist",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Post doesn't exist\" }"))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error - unexpected problem",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class,
                                            example = "{ \"message\": \"Internal server error\"}"))
                    )
            }
    )
    @GetMapping("/count/post/{postId}")
    public int getPostLikesCount(@PathVariable
                                 @Parameter(description = "Post Id to count it's likes")
                                 @Positive(message = "Post Id can't be negative")
                                 long postId) {
        return likeService.getPostLikesCount(postId);
    }
}


