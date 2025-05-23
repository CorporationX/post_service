package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentValidator commentValidator;
    private final CommentService commentService;

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto commentDto) {
        commentValidator.validateDto(commentDto);
        return commentService.createComment(commentDto);
    }

    @PutMapping("/{id}")
    public CommentDto updateCommentContent(@PathVariable long id, @RequestBody CommentDto commentDto){
        commentValidator.validateIdDto(id, commentDto);
        commentValidator.validateContentDto(commentDto);
        return commentService.updateCommentContent(id, commentDto);
    }

    @GetMapping("/all")
    public List<CommentDto> getAllComments (@RequestBody CommentDto commentDto) {
        commentValidator.validatePostDto(commentDto);
        return commentService.getAllComments(commentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable long id) {
        commentValidator.validateCommentId(id);
        commentService.deleteComment(id);
    }
}
