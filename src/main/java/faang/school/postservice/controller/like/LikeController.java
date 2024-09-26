package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{postId}")
    public LikeDto likePost(@PathVariable @Positive Long postId) {
        return likeService.likePost(postId);
    }

    @DeleteMapping("/posts/{postId}")
    public LikeDto removeLikeOnPost(@PathVariable @Positive Long postId) {
        return likeService.removeLikeOnPost(postId);
    }

    @PostMapping("/comments/{commentId}")
    public LikeDto likeComment(@PathVariable @Positive Long commentId) {
        return likeService.likeComment(commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    public LikeDto removeLikeOnComment(@PathVariable @Positive Long commentId) {
        return likeService.removeLikeOnComment(commentId);
    }
}
