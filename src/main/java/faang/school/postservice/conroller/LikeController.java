package faang.school.postservice.conroller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/like_post/{postId}")
    public void addLikeToPost(@Valid LikeDto likeDto, @PathVariable("postId") @NotNull long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/remove_post_like/{postId}")
    public void deleteLikeFromPost(@Valid LikeDto likeDto, @PathVariable("postId") @NotNull long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/like_comment/{commentId}")
    public void addLikeToComment(@Valid LikeDto likeDto, @PathVariable("commentId") @NotNull long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/remove_comment_like/{commentId}")
    public void deleteLikeFromComment(@Valid LikeDto likeDto, @PathVariable("commentId") @NotNull long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/find_like/{postId}")
    public List<LikeDto> findLikesOfPublishedPost(@PathVariable("postId") @NotNull long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }

}
