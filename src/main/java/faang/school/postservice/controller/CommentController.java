package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public CommentDto createComment(@PathVariable Long postId, @Valid @RequestBody CommentDto commentDto) {
        return commentService.createComment(postId, commentDto);
    }
}
