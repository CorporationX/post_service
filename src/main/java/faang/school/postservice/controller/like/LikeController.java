package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.like.RedisPostLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {

    private final LikeService likeService;
    private final UserContext userContext;
    private final RedisPostLikeService redisPostLikeService;

    @PostMapping("/posts/{postId}")
    public ResponseEntity<LikeDto> likePost(
            @PathVariable long postId
    ) {
        return ResponseEntity.ok(likeService.likePost(postId, userContext.getUserId()));
    }

    @PutMapping("/posts/{postId}/unlike")
    public ResponseEntity<Void> unlikePost(@PathVariable long postId) {
        likeService.unlikePost(postId, userContext.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}")
    public ResponseEntity<LikeDto> likeComment(
            @PathVariable long commentId
    ) {
        return ResponseEntity.ok(likeService.likeComment(commentId, userContext.getUserId()));
    }

    @PutMapping("/comments/{commentId}/unlike")
    public ResponseEntity<Void> unlikeComment(@PathVariable long commentId) {
        likeService.unlikeComment(commentId, userContext.getUserId());
        return ResponseEntity.noContent().build();
    }
}
