package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentControllerValidator;
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
    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PostMapping("/{postId}")
    public void createComment(@PathVariable("postId") Long postId,
                              @RequestBody @Valid CommentCreateDto commentCreateDto,
                              BindingResult bindingResult) {
        CommentControllerValidator.validate(bindingResult);
        var entity = commentMapper.toEntity(commentCreateDto);
        commentService.createComment(postId, entity);
    }

    @PatchMapping("{commentId}")
    public void updateComment(@PathVariable("commentId") Long commentId,
                              @RequestBody @Valid CommentUpdateDto commentUpdateDto,
                              BindingResult bindingResult) {
        CommentControllerValidator.validate(bindingResult);
        var entity = commentMapper.toEntity(commentUpdateDto);
        commentService.updateComment(commentId, entity);
    }

    @GetMapping("/{postId}")
    public Collection<CommentDtoResponse> getAllCommentsByPostId(@PathVariable("postId") Long postId) {
        var comments = commentService.getAllCommentsByPostId(postId);
        return commentMapper.toDtos(comments);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.delete(commentId);
    }
}
