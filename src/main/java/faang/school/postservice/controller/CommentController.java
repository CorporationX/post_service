package faang.school.postservice.controller;

import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public CommentDto createComment(@PathVariable long postId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable("id") long commentId) {
        return commentService.getComment(commentId);
    }

    @PutMapping
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/all")
    public List<CommentDto> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/all-by/{postId}")
    public List<CommentDto> getAllCommentsById(@PathVariable("postId") long postId) {
        return commentService.getAllCommentsById(postId);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteComment(@PathVariable("id") long commentId) {
        commentService.deleteComment(commentId);
    }
}
