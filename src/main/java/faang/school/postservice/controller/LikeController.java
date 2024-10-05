package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users by likes on comments and posts",
        description = "Gives capability to get users list on a specified post or comment by likes")
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "Users by post's likes",
            description = "Gives users by likes on specified post id")
    @GetMapping("/posts/{id}/likes")
    public List<UserDto> getAllUsersByPost(@PathVariable @Parameter(description = "Post id",
            required = true) Long id) {
        return likeService.getAllUsersByPostId(id);
    }

    @Operation(summary = "Users by comment's likes",
            description = "Gives users by likes on specified comment id")
    @GetMapping("/comments/{id}/likes")
    public List<UserDto> getAllUsersByComment(@PathVariable @Parameter(description = "Comment id",
            required = true) Long id) {
        return likeService.getAllUsersByCommentId(id);
    }
}
