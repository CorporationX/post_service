package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SortingStrategyDto;
import faang.school.postservice.dto.comment.validation.group.Create;
import faang.school.postservice.dto.comment.validation.group.Update;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments")
    public CommentDto createComment(@PathVariable @Positive Long postId,
                                    @RequestBody @Validated(Create.class) CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }

    @PutMapping("/comments/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long postId,
                                    @PathVariable @Positive Long commentId,
                                    @RequestBody @Validated(Update.class) CommentDto commentDto) {
        return commentService.updateComment(postId, commentId, commentDto);
    }

    @GetMapping("/comments")
    public List<CommentDto> getComments(@PathVariable @Positive Long postId,
                                        @Valid SortingStrategyDto sortingStrategyDto) {
        return commentService.getComments(postId, sortingStrategyDto);
    }

    @DeleteMapping("/comments/{commentId}")
    public CommentDto deleteComment(@PathVariable @Positive Long postId,
                                    @PathVariable @Positive Long commentId) {
        return commentService.deleteComment(postId, commentId);
    }
}
