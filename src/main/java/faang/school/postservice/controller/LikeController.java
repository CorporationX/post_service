package faang.school.postservice.controller;

import faang.school.postservice.config.redis.LikeEventPublisher;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
@Validated
public class LikeController {

    private final LikeService likeService;
    @PostMapping("/post")
    public LikeDto likePost(@RequestBody @Valid LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/post")
    public void unlikePost(@RequestBody @Valid LikeDto likeDto) {
        likeService.unlikePost(likeDto);
    }

    @PostMapping("/comment")
    public LikeDto likeComment(@RequestBody @Valid LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment")
    public void unlikeComment(@RequestBody @Valid LikeDto likeDto) {
        likeService.unlikeComment(likeDto);
    }

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersByPostId(@PathVariable long postId) {
        return likeService.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersByCommentId(@PathVariable long commentId) {
        return likeService.getUsersLikedComment(commentId);
    }

}
