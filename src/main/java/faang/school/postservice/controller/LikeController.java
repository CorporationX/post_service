package faang.school.postservice.controller;

import faang.school.postservice.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}/user/{userId}")
    public LikeDto likePost(@PathVariable long postId, @PathVariable long userId) {
        log.info("Like post {} by user {}", postId, userId);
        return likeService.likePost(postId, userId);
    }

    @DeleteMapping("/posts/{postId}/user/{userId}")
    public void unlikePost(@PathVariable long postId, @PathVariable long userId) {
        log.info("Unlike post {} by user {}", postId, userId);
        likeService.unlikePost(postId, userId);
    }

    @PostMapping("/comment/{commentId}/user/{userId}")
    public LikeDto likeComment(@PathVariable long commentId, @PathVariable long userId) {
        log.info("Like comment {} by user {}", commentId, userId);
        return likeService.likeComment(commentId, userId);
    }

    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public void unlikeComment(@PathVariable long commentId, @PathVariable long userId) {
        log.info("Unlike comment {} by user {}", commentId, userId);
        likeService.unlikeComment(commentId, userId);
    }
}