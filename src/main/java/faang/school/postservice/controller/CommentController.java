package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.InputCommentControllerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final UserContext userContext;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final InputCommentControllerValidator inputCommentControllerValidator;

    @PostMapping("/{postId}")
    public void createComment(@PathVariable("postId") Long postId,
                              @RequestBody @Valid CommentRequestDto commentRequestDto,
                              BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);
        Long authorId = userContext.getUserId();

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(authorId);
        
        commentService.createComment(postId, entity);
    }

    @PatchMapping("{commentId}")
    public void updateComment(@PathVariable("commentId") Long commentId,
                              @RequestBody @Valid CommentRequestDto commentRequestDto,
                              BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);
        Long authorId = userContext.getUserId();

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(authorId);

        commentService.updateComment(commentId, entity);
    }

    @GetMapping("/{postId}")
    public Collection<CommentResponseDto> getAllCommentsByPostId(@PathVariable("postId") Long postId) {
        var comments = commentService.getAllCommentsByPostId(postId);
        return commentMapper.toDtos(comments);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }
}
