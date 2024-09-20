package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/comment")
    public LikeDto addCommentLike(@RequestBody @Valid LikeDto likeDto) {
        return likeService.addCommentLike(likeDto);
    }

    @DeleteMapping("user/{userId}/comment/{commentId}")
    public void deleteCommentLike(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteCommentLike(likeDto);
    }

    @PostMapping("/post")
    public LikeDto addPostLike(@RequestBody @Valid LikeDto likeDto) {
        return likeService.addPostLike(likeDto);
    }

    @DeleteMapping("user/{userId}/post/{postId}")
    public void deletePostLike(@RequestBody @Valid LikeDto likeDto) {
        likeService.deletePostLike(likeDto);
    }

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersByPostId(@PathVariable Long postId) {
        return likeService.findUsersByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersByCommentId(@PathVariable Long commentId) {
        return likeService.findUsersByCommentId(commentId);
    }
}
