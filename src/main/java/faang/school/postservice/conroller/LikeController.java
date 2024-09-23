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

    @PutMapping("/like_post")
    public void addLikeToPost(@Valid LikeDto likeDto, @NotNull long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/remove_post_like")
    public void deleteLikeFromPost(@Valid LikeDto likeDto, @NotNull long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/like_comment")
    public void addLikeToComment(@Valid LikeDto likeDto, @NotNull long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/remove_comment_like")
    public void deleteLikeFromComment(@Valid LikeDto likeDto, @NotNull long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/find_like")
    public List<LikeDto> findLikesOfPublishedPost(@RequestParam("postId") @NotNull long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }

}
