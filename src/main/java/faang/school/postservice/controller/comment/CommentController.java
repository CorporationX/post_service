package faang.school.postservice.controller.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Validated @RequestBody CommentDto dto) {
        return commentService.create(userContext.getUserId(), dto);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CommentDto update(@Validated @RequestBody CommentDto dto) {
        return commentService.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        commentService.delete(id);
    }

    @GetMapping("/postId/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAll(@PathVariable("postId") Long postId) {
        return commentService.findAll(postId);
    }
}