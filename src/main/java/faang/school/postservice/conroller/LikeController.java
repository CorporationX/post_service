package faang.school.postservice.conroller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/like_post")
    public void addLikeToPost(LikeDto likeDto, long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/remove_post_like")
    public void deleteLikeFromPost(LikeDto likeDto, long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/like_comment")
    public void addLikeToComment(LikeDto likeDto, long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/remove_comment_like")
    public void deleteLikeFromComment(LikeDto likeDto, long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/find_like")
    public List<LikeDto> findLikesOfPublishedPost(@RequestParam("postId") long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }

}
