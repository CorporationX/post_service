package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Slf4j
public class CommentController {

    private final CommentService service;

    @PostMapping("/create")
    public CommentDto createComment(@RequestBody @Validated CommentDto commentDto) {
        log.info("Received request to create comment with author ID: {}, and post ID: {}", commentDto.getAuthorId(), commentDto.getPostId());
        return service.createComment(commentDto);
    }

    @PutMapping("/update")
    public CommentDto updateComment(@RequestBody @Validated CommentDto commentDto) {
        log.info("Received request to update comment with author ID: {}, and post ID: {}", commentDto.getAuthorId(), commentDto.getPostId());
        return service.updateComment(commentDto);
    }

    @GetMapping("/{postId}")
    public Page<CommentDto> getCommentsByPost(@PathVariable @NotNull Long postId, @PageableDefault(size = 20) Pageable pageable) {
        log.info("Received request to retrieve comments by Post with ID: {}", postId);
        return service.getCommentsByPost(postId, pageable);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable @NotNull Long commentId) {
        log.info("Received request to delete comment with ID: {}", commentId);
        service.deleteComment(commentId);
    }
}
