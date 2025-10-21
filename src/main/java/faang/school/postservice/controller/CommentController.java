package faang.school.postservice.controller;


import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.SendCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.service.comment.CommentServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/comment")
public class CommentController {
    private final CommentServiceImpl commentService;

    @PostMapping
    public void sendComment(@Valid @RequestBody SendCommentDto sendCommentDto) {
        commentService.sendComment(sendCommentDto);
    }

    @PutMapping("{postId}")
    public void updateComment(@Valid @RequestBody UpdateCommentDto updateCommentDto, @NotNull @PathVariable Long postId) {
        commentService.updateComment(updateCommentDto, postId);
    }

    @GetMapping("/{postId}/comments")
    public List<ResponseCommentDto> getComments(@PathVariable long postId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int pageSize) {
        return commentService.getComments(postId, page, pageSize);
    }

    @DeleteMapping("/delete/{postId}/{commentId}")
    public void deleteComment(@NotNull @PathVariable Long postId, @NotNull @PathVariable Long commentId) {
        commentService.deleteComment(commentId, postId);
    }
}