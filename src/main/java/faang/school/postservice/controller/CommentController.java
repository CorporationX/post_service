package faang.school.postservice.controller;

import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService service;

    @PostMapping("/{postId}")
    public RedisCommentDto createComment(@PathVariable long postId, @RequestBody RedisCommentDto commentDto) {
        return service.createComment(postId, commentDto);
    }

    @PostMapping
    public ResponseEntity<CommentEventDto> create() {
        return ResponseEntity.ok(service.create());
    }
}
