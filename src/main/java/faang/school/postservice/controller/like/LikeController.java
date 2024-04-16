package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Endpoint for likes")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/posts/{postId}/likes")
    @Operation(summary = "Get list users who liked post")
    public List<UserDto> getUsersLikedPost(@PathVariable Long postId) {
        return likeService.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}/likes")
    @Operation(summary = "Get list users who liked comment")
    public List<UserDto> getUsersLikedComment(@PathVariable Long commentId) {
        return likeService.getUsersLikedComment(commentId);
    }
}
