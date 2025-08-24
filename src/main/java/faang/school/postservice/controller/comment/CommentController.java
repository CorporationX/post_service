package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.facade.comment.CommentFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentFacade commentFacade;

    @PostMapping
    public ResponseEntity<CommentDtoResponse> create(@RequestBody @Valid CommentCreateDto dto) {
        CommentDtoResponse created = commentFacade.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping
    public ResponseEntity<CommentDtoResponse> update(@RequestBody @Valid CommentUpdateDto dto) {
        CommentDtoResponse updated = commentFacade.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDtoResponse> getCommentById(@PathVariable long commentId) {
        log.debug("Post controller accepted request get comment with id {}", commentId);

        CommentDtoResponse response = commentFacade.getCommentById(commentId);
        log.debug("Comment controller return response get comment {}", response);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentDtoResponse>> getAllByPostId(@RequestParam Long postId) {
        List<CommentDtoResponse> comments = commentFacade.getAllByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentFacade.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
