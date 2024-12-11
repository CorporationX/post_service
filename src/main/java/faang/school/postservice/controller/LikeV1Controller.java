package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/likes")
public class LikeV1Controller {
    private final LikeService likeService;
    private final UserContext userContext;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikePostDto likePost(@PathVariable @Positive long postId) {
        long actorId = userContext.getUserId();
        validateUserId(actorId);
        return likeService.createLikePost(postId, actorId);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeCommentDto likeComment(@PathVariable @Positive long commentId) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        return likeService.createLikeComment(commentId, userId);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@PathVariable @Positive long postId) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@PathVariable @Positive long commentId) {
        long userId = userContext.getUserId();
        validateUserId(userId);
        likeService.deleteLikeFromComment(commentId, userId);
    }

    private void validateUserId(Long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be more 0");
        }
    }
}