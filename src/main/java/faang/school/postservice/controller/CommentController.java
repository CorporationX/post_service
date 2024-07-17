package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable long postId, @Valid @RequestBody CommentDto commentDto){
        commentDto.setPostId(postId);
        return commentService.createComment(commentDto);
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable long commentId,@Valid @RequestBody CommentDto commentDto){
        return commentService.updateComment(commentId, commentDto);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsForPost(@PathVariable long postId){
        return commentService.getAllCommentsForPost(postId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId,@RequestHeader("userId") long userId){
        commentService.deleteComment(commentId, userId);
    }
}
