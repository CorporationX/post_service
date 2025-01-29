package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    @PostMapping("/posts/like")
    public ResponseEntity<?> toggleLikePost(@Valid @NotNull @RequestBody LikePostRequest request) {
        return service.toggleLikePost(request);
    }

    @PostMapping("/comments/like")
    public ResponseEntity<?> toggleLikeComment(@Valid @NotNull @RequestBody LikeCommentRequest request) {
        return service.toggleLikeComment(request);
    }
}
