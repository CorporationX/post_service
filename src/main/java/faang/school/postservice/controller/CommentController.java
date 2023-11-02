package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
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
@RequestMapping("/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto) {
        log.info("Received request to create comment to post with id={}", commentDto.getPostId());
        return commentService.createComment(commentDto);
    }

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable("id") Long commentId) {
        log.info("Received request to get comment with id={}", commentId);
        return commentService.getComment(commentId);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody @Valid CommentDto commentDto) {
        log.info("Received request to update comment to post with id={}, commentId={}",
                commentDto.getPostId(), commentDto.getId());
        return commentService.updateComment(commentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable("id") Long commentId) {
        log.info("Received request to delete comment from post, commentId={}",commentId );
        commentService.deleteComment(commentId);
    }

    @GetMapping("/by-post-id/{id}")
    public List<CommentDto> getAllComments(@PathVariable("id") Long postId) {
        log.info("Received request to get all comments from post with id={}", postId);
        return commentService.getAllComments(postId);
    }
}
