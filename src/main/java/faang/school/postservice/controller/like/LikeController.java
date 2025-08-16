package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
@Tag(name = "Like", description = "Managing likes related to posts and comments")
public class LikeController {

    private final UserContext userContext;
    private final LikeService likeService;

    @Operation(
            summary = "Like post",
            description = "Like post by ID, user can only put one like"
    )
    @PostMapping("/posts/{postId}")
    public void likePost(@PathVariable @Positive long postId){
        likeService.likePost(userContext.getUserId(), postId);
    }

    @Operation(
            summary = "Like comment",
            description = "Like comment by ID user can only put one like"
    )
    @PostMapping("/comments/{commentId}")
    public void likeComment(@PathVariable @Positive long commentId){
        likeService.likeComment(userContext.getUserId(), commentId);
    }

    @Operation(
            summary = "Remove like from post",
            description = "Remove like from comment by ID, user can only delete a previously placed like"
    )
    @DeleteMapping("/posts/{postId}")
    public void deleteLikeFromPost(@PathVariable @Positive long postId){
        likeService.deleteLikeFromPost(userContext.getUserId(), postId);
    }

    @Operation(
            summary = "Remove like from comment",
            description = "Remove like from comment by ID, user can only delete a previously placed like"
    )
    @DeleteMapping("/comments/{commentId}")
    public void deleteLikeFromComment(@PathVariable @Positive long commentId){
        likeService.deleteLikeFromComment(userContext.getUserId(), commentId);
    }
}
