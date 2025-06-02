package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentServiceFacade commentServiceF;

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentDtoResponse> createComment(@PathVariable long postId,
                                                            @RequestParam String content) {
        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(postId, content);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDtoResponse> updateComment(@PathVariable long commentId,
                                                            @RequestParam String newContent) {
        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentId, newContent);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComments(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Long> deleteComment(@PathVariable long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok(commentId);
    }
}
