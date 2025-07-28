package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.impl.CommentLikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;


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
    public ResponseEntity<Void> unLike(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {
        commentLikeService.removeLike(commentId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentLikeService.getLikeCount(commentId));
    }
}
