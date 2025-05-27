package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
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

    //todo разобраться с возвращаемыми данными в контроллере
    //todo валидация Dto разбраться

    @PostMapping("/comments")
    public ResponseEntity<Void> createComment(@RequestBody CreateCommentDto commentDto) {
        commentServiceF.createComment(commentDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(@RequestBody UpdateCommentDto commentDto) {
        commentServiceF.updateComment(commentDto);
        return ResponseEntity.ok().build();
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
