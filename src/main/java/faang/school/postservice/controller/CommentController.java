package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.InputCommentControllerValidator;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final UserContext userContext;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final InputCommentControllerValidator inputCommentControllerValidator;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@PathVariable Long postId,
                                            @RequestBody @Valid CommentRequestDto commentRequestDto,
                                            BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);
        var authorId = userContext.getUserId();

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(authorId);
        
        var comment =  commentService.createComment(postId, entity);
        return commentMapper.toDto(comment);
    }

    @PatchMapping("{commentId}")
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

    @GetMapping("/{postId}")
    public Collection<CommentResponseDto> getAllCommentsByPostId(@PathVariable Long postId) {
        var comments = commentService.getAllCommentsByPostId(postId);
        return commentMapper.toDtos(comments);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.delete(commentId);
    }
}
