package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
@Tag(name = "Like Controller")
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;

    @Operation(summary = "Get Post Likes", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = false)})
    @GetMapping("/post/{id}")
    public List<UserDto> getPostLikes(@PathVariable long id) {
        return likeService.getPostLikes(id);
    }

    @Operation(summary = "Get Comment Likes", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = false)})
    @GetMapping("/comment/{id}")
    public List<UserDto> getCommentLikes(@PathVariable long id) {
        return likeService.getCommentLikes(id);
    }

    @PostMapping("/post")
    @Operation(summary = "Add like to post", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = false)})
    public LikeDto addLikeToPost(@RequestBody LikeDto like) {
        validateId(like.getPostId());
        return likeService.addLikeToPost(like);
    }

    @Operation(summary = "Add like to comment", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = false)})
    @PostMapping("/comment")
    public LikeDto addLikeToComment(@RequestBody LikeDto like) {
        validateId(like.getCommentId());
        return likeService.addLikeToComment(like);
    }

    @Operation(summary = "Delete like from comment post", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = true)})
    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@PathVariable long postId) {
        long userId = userContext.getUserId();
        validateIds(postId, userId);
        likeService.deleteLikeFromPost(postId, userId);
    }

    @Operation(summary = "Delete like from comment ", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = true)})
    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@PathVariable long commentId) {
        long userId = userContext.getUserId();
        validateIds(commentId, userId);
        likeService.deleteLikeFromComment(commentId, userId);
    }

    private void validateIds(long firstId, long secondId) {
        if (firstId <= 0 || secondId <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }

    private void validateId(long id) {
        if (id <= 0) {
            throw new DataValidationException("Incorrect id");
        }
    }
}