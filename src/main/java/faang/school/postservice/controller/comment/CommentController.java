package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {
    private final CommentServiceFacade commentServiceF;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDtoResponse> createComment(@RequestBody CommentCreateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @PutMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDtoResponse> updateComment(@RequestBody CommentUpdateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @GetMapping(
            value = "/{postId}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComments(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @DeleteMapping(
            value = "/{commentId}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> deleteComment(@PathVariable long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok(commentId);
    }
}
