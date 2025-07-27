package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.LikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final UserContext userContext;
    private final LikeServiceImpl likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        likeService.addLikePost(postId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<Void> likeComment(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {
        likeService.addLikeComment(commentId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removal/post/{postId}")
    public ResponseEntity<Void> likeRemovalPost(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        likeService.removeLikePost(postId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/removal/comment/{commentId}")
    public ResponseEntity<Void> likeRemovalComment(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {
        likeService.removeLikeComment(commentId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Integer> getPostLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(likeService.getPostLikeCount(postId));
    }

    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<Integer> getCommentLikeCount(@PathVariable Long commentId) {
        return ResponseEntity.ok(likeService.getCommentLikeCount(commentId));
    }

}
