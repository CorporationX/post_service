package faang.school.postservice.controller;

import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService service;

    @PostMapping
    public ResponseEntity<CommentEventDto> create() {
        return ResponseEntity.ok(service.create());
    }
}
