package faang.school.postservice.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final InputCommentControllerValidator inputCommentControllerValidator;
    private final static String DEFAULT_USER_ID = "x-user-id";

    @PostMapping("/{postId}")
    public void createComment(@RequestHeader(DEFAULT_USER_ID) Long defaultUser,
                              @PathVariable("postId") Long postId,
                              @RequestBody @Valid CommentRequestDto commentRequestDto,
                              BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(defaultUser);
        
        commentService.createComment(postId, entity);
    }

    @PatchMapping("{commentId}")
    public void updateComment(@RequestHeader(DEFAULT_USER_ID) Long defaultUser,
                              @PathVariable("commentId") Long commentId,
                              @RequestBody @Valid CommentRequestDto commentRequestDto,
                              BindingResult bindingResult) {

        inputCommentControllerValidator.validate(bindingResult);

        var entity = commentMapper.toEntity(commentRequestDto);
        entity.setAuthorId(defaultUser);

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
