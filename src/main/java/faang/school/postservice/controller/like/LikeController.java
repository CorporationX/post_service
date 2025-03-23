package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.interfaces.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable("postId") long postId,
                                            @RequestBody @Valid LikeDto likeDto) {
        LikeDto createdLike = likeService.likePost(postId, likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("postId") long postId,
                                           @RequestBody @Valid LikeDto likeDto) {
        likeService.unlikePost(postId, likeDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable("commentId") long commentId,
                                               @RequestBody @Valid LikeDto likeDto) {
        LikeDto createdLike = likeService.likeComment(commentId, likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable("commentId") long commentId,
                                              @RequestBody @Valid LikeDto likeDto) {
        likeService.unlikeComment(commentId, likeDto);
        return ResponseEntity.noContent().build();
    }
}
