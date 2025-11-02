package faang.school.postservice.controller;

import faang.school.postservice.service.likes.LikeServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class LikeController {

    private final LikeServiceImpl likeService;

    @PostMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public void createPostLike(@PathVariable @Positive Long postId) {
        likeService.createPostLike(postId);
    }

    @DeleteMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePostLike(@PathVariable @Positive Long postId) {
        likeService.deletePostLike(postId);
    }

    @PostMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCommentLike(@PathVariable @Positive Long commentId) {
        likeService.createCommentLike(commentId);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentLike(@PathVariable @Positive Long commentId) {
        likeService.deleteCommentLike(commentId);
    }
}
