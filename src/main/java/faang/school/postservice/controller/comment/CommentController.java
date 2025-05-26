package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping()
    public CommentOutputDto create(@Valid @RequestBody CommentForCreationDto commentDto) {
        return service.create(commentDto);
    }

    @PatchMapping()
    public CommentDto update(@Valid @RequestBody CommentForUpdateDto commentDto) {
        return service.update(commentDto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentOutputDto> findByPostId(@NotNull @PathVariable long postId) {
        return service.findByPostId(postId);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@NotNull @PathVariable long commentId) {
        return service.findById(commentId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteById(@NotNull @PathVariable long commentId) {
        service.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
