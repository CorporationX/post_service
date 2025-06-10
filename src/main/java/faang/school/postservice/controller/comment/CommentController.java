package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("post/userId/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}")
    public ResponseEntity<CommentDto> addComment(@PathVariable Long postId, @RequestBody CommentDto commentDto) {
        if (!isValidCommentDto(commentDto)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        commentDto.setPostId(postId);
        CommentDto addedComment = commentService.addComment(commentDto);
        return new ResponseEntity<>(addedComment, HttpStatus.CREATED);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long postId, @PathVariable Long commentId,
                                                    @RequestBody CommentDto commentDto) {
        if (!isValidCommentDto(commentDto)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        commentDto.setId(commentId);
        CommentDto updatedComment = commentService.updateComment(commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getAllComments(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long posId, @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private boolean isValidCommentDto(CommentDto commentDto) {
        return commentDto != null
                && commentDto.getContent() != null
                && !commentDto.getContent().trim().isEmpty();
    }
}