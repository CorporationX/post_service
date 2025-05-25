package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
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
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentValidator validator;
    private final CommentService service;

    public CommentController(CommentValidator validator, CommentService service) {
        this.validator = validator;
        this.service = service;
    }

    @PostMapping()
    public CommentDto create(@RequestBody CommentDto commentDto){
        commentDto.setStatus(CommentDtoStatus.CREATION);
        if (null != commentDto.getCreatedAt()) {
            throw new DataValidationException
                    ("the createdAt field will be filled in automatically");
        }
        validator.validate(commentDto);
        return service.create(commentDto);
    }

    @PutMapping()
    public CommentDto update(@RequestBody CommentDto commentDto){
        commentDto.setStatus(CommentDtoStatus.UPDATE);
        validator.validate(commentDto);
        return service.update(commentDto);
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> findByPostId(@PathVariable long postId){
        return service.findByPostId(postId);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@PathVariable long commentId){
        return service.findById(commentId);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteById(@PathVariable long commentId){
        service.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
