package faang.school.postservice.controller.comment;

import faang.school.postservice.controller.ApiPath;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.COMMENT)
public class CommentController {
    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<CommentDto> addComment(@RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addNewCommentInPost(commentDto));
    }

    @PutMapping()
    public ResponseEntity<CommentDto> updateComment(@RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateExistingComment(commentDto));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @DeleteMapping()
    public ResponseEntity<CommentDto> deleteComment(@RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.deleteExistingCommentInPost(commentDto));
    }

}
