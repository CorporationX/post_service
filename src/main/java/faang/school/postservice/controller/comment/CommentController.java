package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.facade.CommentFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacade commentFacade;

    @PostMapping
    public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateDto dto) {
        CommentDto created = commentFacade.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping
    public ResponseEntity<CommentDto> update(@RequestBody @Valid CommentUpdateDto dto) {
        CommentDto updated = commentFacade.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getAllByPostId(@PathVariable Long postId) {
        List<CommentDto> comments = commentFacade.getAllByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId) {
        commentFacade.delete(commentId);
        return ResponseEntity.noContent().build();
    }
}
