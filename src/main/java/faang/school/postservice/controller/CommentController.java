package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.util.ErrorMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentDto create(@Valid CommentDto commentDto){
        if (commentDto != null){
            throw new DataValidationException(ErrorMessage.COMMENT_ID_NOT_NULL_ON_CREATION);
        }
        return commentService.create(commentDto);
    }

    @PutMapping
    public CommentDto update(@Valid CommentDto commentDto){
        return commentService.update(commentDto);
    }
}