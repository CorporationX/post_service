package faang.school.postservice.controller;

import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/posts/{postId}/user/{userId}")
    public void likePost(@PathVariable long postId, @PathVariable long userId) {
        log.info("Like post {} by user {}", postId, userId);
        likeService.likePost(postId, userId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/posts/{postId}/user/{userId}")
    public void unlikePost(@PathVariable long postId, @PathVariable long userId) {
        log.info("Unlike post {} by user {}", postId, userId);
        likeService.unlikePost(postId, userId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/comment/{commentId}/user/{userId}")
    public void likeComment(@PathVariable long commentId, @PathVariable long userId) {
        log.info("Like comment {} by user {}", commentId, userId);
        likeService.likeComment(commentId, userId);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public void unlikeComment(@PathVariable long commentId, @PathVariable long userId) {
        log.info("Unlike comment {} by user {}", commentId, userId);
        likeService.unlikeComment(commentId, userId);
    }
}