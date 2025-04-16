package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentLikeDto;
import faang.school.postservice.dto.PostLikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/users/{userId}")
    public ResponseEntity<PostLikeDto> likePost(@PathVariable Long userId,
                                                @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(likeService.likePost(userId, postId));
    }

    @PostMapping("/comments/{commentId}/users/{userId}")
    public ResponseEntity<CommentLikeDto> likeComment(@PathVariable Long userId,
                                                      @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(likeService.likeComment(userId, commentId));
    }

    @DeleteMapping("/posts/{postId}/users/{userId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long userId,
                                           @PathVariable Long postId) {
        likeService.unlikePost(userId, postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}/users/{userId}")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long userId,
                                              @PathVariable Long commentId) {
        likeService.unlikeComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }
}

