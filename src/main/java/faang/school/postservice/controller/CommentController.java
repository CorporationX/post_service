package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.InputCommentControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@Tag(name = "Comments controller", description = "Main comments operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final UserContext userContext;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final InputCommentControllerValidator inputCommentControllerValidator;

    @Operation(summary = "Create comment")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Comment created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input dto fields",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable Long postId,
                                            @RequestBody @Valid CommentRequestDto commentRequestDto,
                                            BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);
        var authorId = userContext.getUserId();

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(authorId);

        var comment = commentService.createComment(postId, entity);
        return commentMapper.toDto(comment);
    }

    @Operation(summary = "Update comment by id")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment updated",
                            content = @Content(
                                    schema = @Schema(implementation = CommentResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input dto fields",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long commentId,
                                            @RequestBody @Valid CommentRequestDto commentRequestDto,
                                            BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);
        var authorId = userContext.getUserId();

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(authorId);

        var comment = commentService.updateComment(commentId, entity);
        return commentMapper.toDto(comment);
    }

    @Operation(summary = "Getting comments by post")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Return list of comments",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = CommentResponseDto.class)
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{postId}")
    public Collection<CommentResponseDto> getAllCommentsByPostId(@PathVariable Long postId) {
        var comments = commentService.getAllCommentsByPostId(postId);
        return commentMapper.toDtos(comments);
    }

    @Operation(summary = "Delete comment")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment deleted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }
}
