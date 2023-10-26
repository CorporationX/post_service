package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
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
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public CommentDto create(@RequestBody CommentDto commentDto) {
        return commentService.createComment(commentDto);
    }

    @GetMapping("/{id}/get")
    public CommentDto getComment(@PathVariable("id") long commentId) {
        return commentService.getComment(commentId);
    }

    @PutMapping("/update")
    public CommentDto updateComment(@RequestBody CommentDto commentDto) {
        return commentService.updateComment(commentDto);
    }

    @GetMapping("/{id}/get/all")
    public List<CommentDto> getAllComments(@PathVariable("id") long postId) {
        return commentService.getAllComments(postId);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteComment(@PathVariable("id") long commentId) {
        commentService.deleteComment(commentId);
    }
}
