package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}")
    public void putLikeOnPost(@PathVariable Long postId) {
        likeService.putLikeOnPost(postId);
    }

    @DeleteMapping("/posts/{postId}")
    public void removeLikeAtPost(@PathVariable Long postId) {
        likeService.removeLikeAtPost(postId);
    }

    @PostMapping("/comments/{commentId}")
    public void putLikeOnComment(@PathVariable Long commentId) {
        likeService.putLikeOnComment(commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void removeLikeAtComment(@PathVariable Long commentId) {
        likeService.removeLikeAtComment(commentId);
    }
}
