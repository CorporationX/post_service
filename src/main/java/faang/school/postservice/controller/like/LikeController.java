package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.interfaces.LikeService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable("postId") @Min(1) long postId,
                                            @RequestHeader("x-user-id") @NotNull @Min(1) Long headerUserId) {
        LikeDto createdLike = likeService.likePost(postId, headerUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("postId") @Min(1) long postId,
                                           @RequestHeader("x-user-id") @NotNull @Min(1) Long headerUserId) {
        likeService.unlikePost(postId, headerUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable("commentId") @Min(1) long commentId,
                                               @RequestHeader("x-user-id") @NotNull @Min(1) Long headerUserId) {
        LikeDto createdLike = likeService.likeComment(commentId, headerUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable("commentId") @Min(1) long commentId,
                                              @RequestHeader("x-user-id") @NotNull @Min(1) Long headerUserId) {
        likeService.unlikeComment(commentId, headerUserId);
        return ResponseEntity.noContent().build();
    }
}
