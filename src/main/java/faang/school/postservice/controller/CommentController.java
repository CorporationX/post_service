package faang.school.postservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor

public class CommentController {

    private final CommentService commentService;

    @PostMapping("/post/{postId}")
    public CommentDto createComment(@PathVariable Long postId ,@Valid @RequestBody CommentDto commentDto){
        log.info("Received request to create comment from authorId ID: {} to postId ID: {} .",commentDto.getAuthorId(),postId);
        return commentService.createComment(postId,commentDto);
    }
    @PutMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,@Valid @RequestBody CommentDto commentDto){
        log.info("Received request to update comment ID: {} with new data: {}.", commentId,commentDto);
        return commentService.updateComment(commentId,commentDto);
    }
    @GetMapping("/posts/{postId}")
    public List<CommentDto> getAllComments(@PathVariable Long postId){
        log.info("Received request to retrieve all comments for post ID: {}",postId);
        return commentService.getAllComments(postId);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCommentById(@PathVariable Long id){
        log.info("Received request to delete comment  ID: {}",id);
        commentService.deleteCommentById(id);
        return ResponseEntity.ok( "Comment is deleted successfully");
    }
}
