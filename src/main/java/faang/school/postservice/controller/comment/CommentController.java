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

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long postId,
                                                           @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.addNewCommentInPost(postId, commentDto));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long postId,
                                                              @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.updateExistingComment(postId, commentDto));
    }

    @GetMapping("/get/{postId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long postId,
                                                              @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(commentService.deleteExistingCommentInPost(postId, commentDto));
    }

}
