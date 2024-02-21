package faang.school.postservice.controller;

import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public CommentDto createComment(@PathVariable long postId, @RequestBody CommentDto commentDto) {
        log.info("Received to create comment for post: {}", postId);
        return commentService.createComment(postId, commentDto);
    }

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable("id") long commentId) {
        log.info("Received to get comment with id: {}", commentId);
        return commentService.getComment(commentId);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        log.info("Received to update comment with id: {}", commentDto.getId());
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/by-post/{postId}")
    public Page<CommentDto> getAllCommentsById(Pageable pageable, @PathVariable long postId) {
        log.info("Received to get all comments for post: {}", postId);
        return commentService.getAllCommentsById(pageable, postId);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") long commentId) {
        log.info("Received to delete comment with id: {}", commentId);
        commentService.deleteComment(commentId);
    }
}
