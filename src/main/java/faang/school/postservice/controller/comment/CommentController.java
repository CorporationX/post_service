package faang.school.postservice.controller.comment;

import faang.school.postservice.model.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentControllerValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Tag(name = "CommentController", description = "Controller for creating, updating, receiving, deleting comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentControllerValidator validator;

    @Operation(description = "This method check dto, if all Ok create comment and return CommentDto")
    @PostMapping()
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @Parameter(description = "Auth header x-user-id", required = true, name = "x-user-id")
                                    @RequestHeader(value = "x-user-id") Long userId) {
        validator.validateCommentDtoNotNull(commentDto);
        validator.validateCommentContentNotNull(commentDto);
        validator.validateCommentPostIdNotNull(commentDto);
        validator.validateCommentAuthorIdNotNull(userId);
        return commentService.createComment(commentDto, userId);
    }

    @Operation(description = "This method return list of CommentDTOs by postId")
    @GetMapping("/{postId}")
    public List<CommentDto> getComment(@PathVariable Long postId) {
        return commentService.getComment(postId);
    }

    @Operation(description = "This method delete comment by id")
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @Operation(description = "This method update field \"content\" in comment")
    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId, @RequestBody CommentDto commentDto,
                                    @Parameter(description = "Auth header x-user-id", required = true, name = "x-user-id")
                                    @RequestHeader(value = "x-user-id") Long userId) {
        validator.validateCommentDtoNotNull(commentDto);
        validator.validateCommentContentNotNull(commentDto);
        return commentService.updateComment(commentId, commentDto, userId);
    }
}
