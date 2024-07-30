package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/comment")
    public LikeDto addCommentLike(@RequestBody @Valid LikeDto likeDto) {
        return likeService.addCommentLike(likeDto);
    }

    @DeleteMapping("user/{userId}/comment/{commentId}")
    public void deleteCommentLike(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        likeService.deleteCommentLike(userId, commentId);
    }

    @PostMapping("/post")
    public LikeDto addPostLike(@RequestBody @Valid LikeDto likeDto) {
        return likeService.addPostLike(likeDto);
    }

    @DeleteMapping("user/{userId}/post/{postId}")
    public void deletePostLike(@PathVariable Long userId,
                               @PathVariable Long postId) {
        likeService.deletePostLike(userId, postId);
    }
}
