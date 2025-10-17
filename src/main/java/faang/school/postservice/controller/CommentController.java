package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<ResponseCommentDto> getAllComments(@PathVariable long postId) {
        log.info("Getting all comments for post with id: {}", postId);
        return commentService.getAllComments(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseCommentDto createComment(
            @PathVariable long postId,
            @Valid @RequestBody CreateCommentDto createCommentDto
    ) {
        log.info("Creating comment for post {} by author {}", postId, createCommentDto.authorId());
        return commentService.createComment(postId, createCommentDto);
    }

    @PutMapping("/{commentId}")
    public ResponseCommentDto updateComment(
            @PathVariable long postId,
            @PathVariable long commentId,
            @Valid @RequestBody UpdateCommentDto updateCommentDto
    ) {
        log.info("Updating comment {} for post {}", commentId, postId);
        return commentService.updateComment(postId, commentId, updateCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable long postId,
            @PathVariable long commentId
    ) {
        log.info("Deleting comment {} for post {}", commentId, postId);
        commentService.deleteComment(postId, commentId);
    }
}