package faang.school.postservice.controller;

import faang.school.postservice.service.likes.LikeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private final LikeServiceImpl likeService;

    @PostMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPostLike(@PathVariable long postId) {
        likeService.createPostLike(postId);
    }

    @DeleteMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostLike(@PathVariable long postId) {
        likeService.deletePostLike(postId);
    }

    @PostMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCommentLike(@PathVariable long commentId) {
        likeService.createCommentLike(commentId);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentLike(@PathVariable long commentId) {
        likeService.deleteCommentLike(commentId);
    }
}
