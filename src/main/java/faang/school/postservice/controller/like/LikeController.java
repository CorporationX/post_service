package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.interfaces.LikeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeServiceImpl;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable("postId") @Min(1) long postId,
                                            @RequestHeader("x-user-id") @Min(1) long userId) {
        LikeDto createdLike = likeServiceImpl.likePost(postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("postId") @Min(1) long postId,
                                           @RequestHeader("x-user-id") @Min(1) long userId) {
        likeServiceImpl.unlikePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable("commentId") @Min(1) long commentId,
                                               @RequestHeader("x-user-id") @Min(1) long userId) {
        LikeDto createdLike = likeServiceImpl.likeComment(commentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLike);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable("commentId") @Min(1) long commentId,
                                              @RequestHeader("x-user-id") @Min(1) long userId) {
        likeServiceImpl.unlikeComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/likes/post/{postId}")
    public ResponseEntity<List<UserDto>> getLikesByPostId(@PathVariable("postId") Long postId) {
        List<UserDto> users = likeServiceImpl.getUserLikedPost(postId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/likes/comment/{commentId}")
    public ResponseEntity<List<UserDto>> getLikesByCommentId(@PathVariable("commentId") Long commentId) {
        List<UserDto> users = likeServiceImpl.getUserLikedComment(commentId);
        return ResponseEntity.ok(users);
    }
}
