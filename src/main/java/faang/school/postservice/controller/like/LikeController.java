package faang.school.postservice.controller.like;

import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.PositiveId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Void> addLikeToPost(@PathVariable @PositiveId long postId, @PathVariable @PositiveId long userId) {
        likeService.addLikeToPost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<Void> removeLikeFromPost(@PathVariable @PositiveId long postId, @PathVariable @PositiveId long userId) {
        likeService.removeLikeFromPost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<Void> addLikeToComment(@PathVariable @PositiveId long commentId, @PathVariable @PositiveId long userId) {
        likeService.addLikeToComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<Void> removeLikeFromComment(@PathVariable @PositiveId long commentId, @PathVariable @PositiveId long userId) {
        likeService.removeLikeFromComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Integer> getCountLikeForPost(@PathVariable @PositiveId Long postId) {
        return ResponseEntity.ok(likeService.getCountLikeForPost(postId));
    }

    @GetMapping("/count/comment/{commentId}")
    public ResponseEntity<Integer> getCountLikeForComment(@PathVariable @PositiveId Long commentId) {
        return ResponseEntity.ok(likeService.getCountLikeForComment(commentId));
    }

    @GetMapping("/count/user/{userId}/post")
    public ResponseEntity<Integer> getCountLikeUserForPosts(@PathVariable @PositiveId Long userId) {
        return ResponseEntity.ok(likeService.getCountLikeUserForPosts(userId));
    }

    @GetMapping("/count/user/{userId}/comment")
    public ResponseEntity<Integer> getCountLikeUserForComments(@PathVariable @PositiveId Long userId) {
        return ResponseEntity.ok(likeService.getCountLikeUserForComments(userId));
    }
}
