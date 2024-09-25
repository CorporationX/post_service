package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public LikeDto likePost(@PathVariable long postId, @RequestBody LikeDto likeDto) {
        return likeService.likePost(postId, likeDto);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@PathVariable long postId, LikeDto likeDto) {
        likeService.deleteLikeFromPost(postId, likeDto);
    }

    @PostMapping("/comment/{commentId}")
    public LikeDto likeComment(@PathVariable long commentId, @RequestBody LikeDto likeDto) {
        return likeService.likeComment(commentId, likeDto);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@PathVariable long commentId, LikeDto likeDto) {
        likeService.deleteLikeFromComment(commentId, likeDto);
    }
}
