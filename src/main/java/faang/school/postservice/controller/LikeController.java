package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/user/{userId}/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likePost(@PathVariable Long postId, @PathVariable Long userId) {
        return likeService.likePost(postId, userId);
    }

    @DeleteMapping("/user/{userId}/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void unlikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.unlikePost(postId, userId);
    }

    @PostMapping("/user/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        return likeService.likeComment(commentId, userId);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public void unlikeComment(@PathVariable Long commentId, @PathVariable Long userId) {
        likeService.unlikeComment(commentId, userId);
    }
}