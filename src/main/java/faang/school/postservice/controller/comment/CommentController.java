package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class CommentController {
    private final CommentServiceFacade commentServiceF;

    @PostMapping("/comments")
    public ResponseEntity<CommentDtoResponse> createComment(@RequestBody CommentCreateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDtoResponse> updateComment(@RequestBody CommentUpdateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDtoResponse>> getAllComment(long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComment(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @DeleteMapping("/comments")
    public ResponseEntity<Void> deleteComment(long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok().build();

    }
}
