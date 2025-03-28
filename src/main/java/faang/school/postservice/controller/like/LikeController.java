package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.interfaces.LikeService;
import jakarta.validation.constraints.Min;
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
                                            @RequestHeader("x-user-id") @Min(1) long userId) {
        LikeDto createdLike = likeService.likePost(postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("postId") @Min(1) long postId,
                                           @RequestHeader("x-user-id") @Min(1) long userId) {
        likeService.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable("commentId") @Min(1) long commentId,
                                               @RequestHeader("x-user-id") @Min(1) long userId) {
        LikeDto createdLike = likeService.likeComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable("commentId") @Min(1) long commentId,
                                              @RequestHeader("x-user-id") @Min(1) long userId) {
        likeService.unlikeComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
