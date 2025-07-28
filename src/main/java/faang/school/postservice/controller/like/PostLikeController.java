package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.impl.PostLikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/post")
public class PostLikeController {
    private final UserContext userContext;
    private final PostLikeServiceImpl postLikeService;


    @PostMapping("/{postId}")
    public ResponseEntity<Void> like(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        postLikeService.addLike(postId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> likeRemoval(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        postLikeService.removeLike(postId, userContext.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/count")
    public ResponseEntity<Integer> getLikeCount(@PathVariable("postID") @Validated @NotNull @NotBlank Long postId) {
        return ResponseEntity.ok(postLikeService.getLikeCount(postId));
    }

}
