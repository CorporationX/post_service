package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

private final LikeService likeService;

    @PostMapping("/post/{postId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.addLikePost(postId, userId);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.removeLikePost(postId, userId);
    }

    @PostMapping("/comment/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
    }
}
