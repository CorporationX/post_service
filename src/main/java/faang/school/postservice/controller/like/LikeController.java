package faang.school.postservice.controller.like;

import faang.school.postservice.dto.LikeCountDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import faang.school.postservice.dto.LikeCountDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public LikeDto putLikeToPost(@PathVariable long postId) {
        return likeService.putLikeToPost(postId);
    }

    @PostMapping("/comment/{commentId}")
    public LikeDto putLikeToComment(@PathVariable long commentId) {
        return likeService.putLikeToComment(commentId);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeForPost(@PathVariable long postId) {
        likeService.deleteLikeForPost(postId);
    }

    @DeleteMapping("/post/{commentId}")
    public void deleteLikeForComment(@PathVariable long commentId) {
        likeService.deleteLikeForComment(commentId);
    }

    @GetMapping("/user")
    public List<LikeDto> getLikesByUser() {
        return likeService.getLikesByUser();
    }

    @GetMapping("/post/{postId}/count")
    public LikeCountDto countLikes(@PathVariable Long postId) {
        return likeService.countLikesForPost(postId);
    }

}
