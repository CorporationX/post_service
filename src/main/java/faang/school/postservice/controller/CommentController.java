package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/comment")
@RequiredArgsConstructor

public class CommentController {
    private final CommentService commentService;

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        commentService.delete(commentId);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getAllCommentsByPostId(@PathVariable long postId) {
        return commentService.getAllCommentsByPostId(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.UPGRADE_REQUIRED)
    public CommentDto updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDto) {
        return commentService.updateComment(updateCommentDto);
    }
}
