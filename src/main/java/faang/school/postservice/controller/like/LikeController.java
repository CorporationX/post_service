package faang.school.postservice.controller.like;

import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Tag(name = "Like Management", description = "Operations related to likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/user/{userId}")
    public void addLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.addLikePost(postId, userId);
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.removeLikePost(postId, userId);
    }

    @PostMapping("/comment/{commentId}/user/{userId}")
    public void addLikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.addLikeComment(commentId, userId);
    }

    @DeleteMapping("/comment/{commentId}/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.removeLikeComment(commentId, userId);
    }
}
