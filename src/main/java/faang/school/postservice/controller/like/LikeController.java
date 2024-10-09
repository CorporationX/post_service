package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;

import faang.school.postservice.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
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

    @PostMapping
    public LikeResponseDto addLike(@Valid @RequestBody LikeRequestDto likeRequestDto) {
        return likeService.addLike(likeRequestDto);
    }

    @DeleteMapping("/{likeId}")
    public void removeLike(@PathVariable @NotNull Long likeId) {
        likeService.removeLike(likeId);
    }
}


