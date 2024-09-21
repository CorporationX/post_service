package faang.school.postservice.controller.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add")
    public ResponseEntity<CommentDto> addComment(@Validated @RequestBody CommentDto commentDto) {
        log.info("Creating comment: {}", commentDto);
        CommentDto createdComment = commentService.addComment(commentDto);

        return ResponseEntity.ok(createdComment);
    }
}
