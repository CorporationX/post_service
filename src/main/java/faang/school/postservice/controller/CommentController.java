package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.CommentDtoStatus;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.CommentValidator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private CommentValidator validator;
    private CommentService service;

    @PostMapping("/{creatorId}")
    public CommentDto create(@PathVariable long creatorId, @RequestBody CommentDto commentDto){
        commentDto.setStatus(CommentDtoStatus.CREATION);
        if (null != commentDto.getAuthorId() || null != commentDto.getCreatedAt()) {
            throw new DataValidationException
                    ("the author and createdAt fields are filled in automatically");
        }
        validator.validate(commentDto);
        return service.create(creatorId, commentDto);
    }

    @PutMapping()
    public CommentDto update(@RequestBody CommentDto commentDto){
        commentDto.setStatus(CommentDtoStatus.UPDATE);
        validator.validate(commentDto);
        return service.update(commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto findById(@PathVariable long commentId){
        return service.findById(commentId);
    }

    @DeleteMapping("/{commentId}")
    public void deleteById(@PathVariable long commentId){
        service.deleteById(commentId);
    }
}
