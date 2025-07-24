package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.LikeServiceImpl;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final UserContext userContext;
    private final LikeServiceImpl likeService;

    @PostMapping("/post/{postId}")
    public String likePost(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {
        likeService.addLikePost(postId, userContext.getUserId());
        return "Post liked successfully";
    }

    @PostMapping("/comment/{commentId}")
    public String likeComment(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {

        return "Comment liked successfully";
    }

    @PostMapping("/removal/post/{postId}")
    public String likeRemovalPost(@PathVariable("postId") @Validated @NotNull @NotBlank Long postId) {

        return "Reply liked successfully";
    }

    @PostMapping("/removal/comment/{commentId}")
    public String likeRemovalComment(@PathVariable("commentId") @Validated @NotNull @NotBlank Long commentId) {

        return "Comment liked successfully";
    }

}
