package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentDto create(CommentDto commentDto) {
        return commentService.create(commentDto);
    }

    public CommentDto update(CommentDto commentDto, long id) {
        return commentService.update(commentDto, id);
    }

    public void delete(CommentDto commentDto, long id) {
        commentService.delete(commentDto, id);
    }

    public List<CommentDto> getAllCommentsById(long id) {
        return commentService.getAllCommentsById(id);
    }
}
