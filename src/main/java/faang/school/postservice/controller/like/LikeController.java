package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    private static final String MESSAGE = "cannot be less than 1";

    @PutMapping("/post/{postId}/user/{userId}")
    public void likeThePost(@PathVariable @Min(value = 1, message = MESSAGE) long postId,
                            @PathVariable @Min(value = 1, message = MESSAGE) long userId) {
        likeService.likeThePost(postId, userId);
    }

    @PutMapping("/comment/{commentId}/user/{userId}")
    public void likeTheComment(@PathVariable @Min(value = 1, message = MESSAGE) long commentId,
                               @PathVariable @Min(value = 1, message = MESSAGE) long userId) {
        likeService.likeTheComment(commentId, userId);
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    public void removeLikeFromPost(@PathVariable @Min(value = 1, message = MESSAGE) long postId,
                                   @PathVariable @Min(value = 1, message = MESSAGE) long userId) {
        likeService.removeLikeFromPost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public void removeLikeFromComment(@PathVariable @Min(value = 1, message = MESSAGE) long commentId,
                                      @PathVariable @Min(value = 1, message = MESSAGE) long userId) {
        likeService.removeLikeFromComment(commentId, userId);
    }
}
