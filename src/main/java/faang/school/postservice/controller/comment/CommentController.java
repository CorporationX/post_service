package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.ChangeCommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "Add comment")
    @PostMapping("/create")
    public CreateCommentDto createComment(@RequestBody @Valid CreateCommentDto createCommentDto) {
        log.info("Received create comment request {}", createCommentDto);
        return commentService.createComment(createCommentDto);
    }

    @Operation(summary = "change comment")
    @PutMapping("/change")
    public CreateCommentDto changeComment(@RequestBody @Valid ChangeCommentDto changeCommentDto) {
        log.info("Received change comment request {}", changeCommentDto);
        return commentService.changeComment(changeCommentDto);
    }

    @Operation(summary = "Get list of commentDto")
    @GetMapping("/post/{id}")
    public List<CreateCommentDto> getAllCommentsOnPostId(@PathVariable long id) {
        log.info("Received information for get all comments on post with id: {}", id);
        return commentService.getAllCommentsOnPostId(id);
    }

    @Operation(summary = "Delete comment")
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable long id) {
        log.info("Received information for delete comment with id: {}", id);
        commentService.deleteComment(id);
    }
}
