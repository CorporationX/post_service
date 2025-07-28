package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.impl.CommentLikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/comment")
public class CommentLikeController {
    private final UserContext userContext;
    private final CommentLikeServiceImpl commentLikeService;

    @PostMapping("/{commentId}")
    public ResponseEntity<Void> like(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {
        commentLikeService.addLike(commentId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> likeRemoval(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {
        commentLikeService.removeLike(commentId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentLikeService.getLikeCount(commentId));
    }
}
