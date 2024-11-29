package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService service;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersLikedPost(@PathVariable @Positive Long postId,
                                           @RequestHeader(name = "x-user-id") Long header) {
        return service.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikedComm(@PathVariable @Positive Long commentId,
                                           @RequestHeader(name = "x-user-id") Long header) {
        return service.getUsersLikedComm(commentId);
    }

    @PutMapping("/like_post/{postId}")
    public void addLikeToPost(@Valid @RequestBody LikeDto likeDto, @PathVariable("postId") long postId) {
        service.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/post_like/{postId}")
    public void deleteLikeFromPost(@Valid LikeDto likeDto, @PathVariable("postId") long postId) {
        service.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/comment_like/{commentId}")
    public void addLikeToComment(@Valid LikeDto likeDto, @PathVariable("commentId") long commentId) {
        service.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/comment_like/{commentId}")
    public void deleteLikeFromComment(@Valid LikeDto likeDto, @PathVariable("commentId") long commentId) {
        service.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/published_likes/{postId}")
    public List<LikeDto> findLikesOfPublishedPost(@PathVariable("postId") long postId) {
        return service.getLikesForPublishedPost(postId);
    }
}

