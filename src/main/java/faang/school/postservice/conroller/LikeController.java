package faang.school.postservice.conroller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/like_post/{postId}")
    public void addLikeToPost(@Valid LikeDto likeDto, @PathVariable("postId") long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/post_like/{postId}")
    public void deleteLikeFromPost(@Valid LikeDto likeDto, @PathVariable("postId") long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/comment_like/{commentId}")
    public void addLikeToComment(@Valid LikeDto likeDto, @PathVariable("commentId") long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/comment_like/{commentId}")
    public void deleteLikeFromComment(@Valid LikeDto likeDto, @PathVariable("commentId") long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/like/{postId}")
    public List<LikeDto> findLikesOfPublishedPost(@PathVariable("postId") long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }
}
