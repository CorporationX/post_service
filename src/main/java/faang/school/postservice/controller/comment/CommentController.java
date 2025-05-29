package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public interface CommentController {
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CommentDtoResponse> createComment(@RequestBody CommentCreateDto commentDto);

    @PutMapping(produces = APPLICATION_JSON_VALUE)
    ResponseEntity<CommentDtoResponse> updateComment(@RequestBody CommentUpdateDto commentDto);

    @GetMapping(value = "/{postId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId);

    @DeleteMapping(value = "/{commentId}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<Long> deleteComment(@PathVariable long commentId);
}
