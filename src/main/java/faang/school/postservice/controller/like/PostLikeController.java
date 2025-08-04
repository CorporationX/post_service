package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.impl.PostLikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/post")
public class PostLikeController {

    private final UserContext userContext;
    private final PostLikeServiceImpl postLikeService;

    @PostMapping("/{postId}")
    public void like(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        postLikeService.addLike(postId, userContext.getUserId());
    }

    @DeleteMapping("/{postId}")
    public void unLike(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        postLikeService.removeLike(postId, userContext.getUserId());
    }

    @GetMapping("/{postId}/count")
    public int getLikeCount(@PathVariable("postID") @Validated @NotNull @NotBlank Long postId) {
        return postLikeService.getLikeCount(postId);
    }

}
