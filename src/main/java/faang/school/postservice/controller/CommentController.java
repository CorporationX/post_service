package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.comment.CommentUpdateMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
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
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final CommentUpdateMapper commentUpdateMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@Valid @RequestBody CommentDto commentResponseDto) {
        Comment commentFromDto = commentMapper.toEntity(commentResponseDto);
        Comment createdComment = commentService.createComment(commentFromDto);
        return commentMapper.toDto(createdComment);
    }

    @PutMapping
    public CommentUpdateDto updateComment(@Valid @RequestBody CommentUpdateDto commentUpdateDto) {
        Comment commentFromUpdateDto = commentUpdateMapper.toEntity(commentUpdateDto);
        Comment updatedComment = commentService.updateComment(commentFromUpdateDto);
        return commentUpdateMapper.toDto(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> findAllComments(@PathVariable long postId) {
        List<Comment> commentList = commentService.findAllComments(postId);
        return commentMapper.toDtoList(commentList);
    }
}
