package faang.school.postservice.conroller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    @PutMapping("/likes")
    public void addLikeToPost(LikeDto likeDto, long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    @DeleteMapping("/likes")
    public void deleteLikeFromPost(LikeDto likeDto, long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    @PutMapping("/likes")
    public void addLikeToComment(LikeDto likeDto, long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    @DeleteMapping("/likes")
    public void deleteLikeFromComment(LikeDto likeDto, long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }

    @GetMapping("/likes")
    public List<Like> findLikesOfPublishedPost(@RequestParam("postId") long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }

}
