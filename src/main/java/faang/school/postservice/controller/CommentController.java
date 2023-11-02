package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@RequestBody CommentDto commentDto) {
        log.debug("Endpoint create comment was called successfully");
        return commentService.createComment(commentDto);
    }

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable("id") long commentId) {
        log.debug("Endpoint get comment was called successfully");
        return commentService.getComment(commentId);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        log.debug("Endpoint update comment was called successfully");
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/by-post-id/{id}")
    public List<CommentDto> getAllComments(@PathVariable("id") long postId) {
        log.debug("Endpoint get all comments was called successfully");
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") long commentId) {
        log.debug("Endpoint delete comment was called successfully");
        commentService.deleteComment(commentId);
    }
}
