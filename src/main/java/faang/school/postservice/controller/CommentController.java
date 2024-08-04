package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.transfer.New;
import faang.school.postservice.dto.transfer.UpdateContent;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/comment/")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public void createComment(@RequestBody @Validated(New.class) CommentDto commentDto) {
        commentService.createComment(commentDto);
    }

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public void updateComment(@RequestBody @Validated(UpdateContent.class) CommentDto commentDto) {
        commentService.updateComment(commentDto);
    }

    @GetMapping("all/{postId}")
    public List<CommentDto> getAllByPostId(@PathVariable Long postId) {
        return commentService.getAllByPostId(postId);
    }

    @DeleteMapping("{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }
}