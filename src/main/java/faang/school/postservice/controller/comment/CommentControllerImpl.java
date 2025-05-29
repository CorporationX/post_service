package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentControllerImpl implements CommentController {
    private final CommentServiceFacade commentServiceF;

    @Override
    public ResponseEntity<CommentDtoResponse> createComment(@RequestBody CommentCreateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.createComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @Override
    public ResponseEntity<CommentDtoResponse> updateComment(@RequestBody CommentUpdateDto commentDto) {
        CommentDtoResponse commentDtoResponse = commentServiceF.updateComment(commentDto);
        return ResponseEntity.ok(commentDtoResponse);
    }

    @Override
    public ResponseEntity<List<CommentDtoResponse>> getAllComments(@PathVariable long postId) {
        List<CommentDtoResponse> commentDtoResponseList = commentServiceF.getAllComments(postId);
        return ResponseEntity.ok(commentDtoResponseList);
    }

    @Override
    public ResponseEntity<Long> deleteComment(@PathVariable long commentId) {
        commentServiceF.deleteComment(commentId);
        return ResponseEntity.ok(commentId);
    }
}
