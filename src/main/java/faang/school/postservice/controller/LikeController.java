package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;


    @PostMapping("/post")
    public LikeDto likePost(@RequestBody LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/post/{postId}")
    public void unlikePost(@PathVariable long postId) {
        likeService.unlikePost(postId);
    }

    @PostMapping("/comment")
    public LikeDto likeComment(@RequestBody LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}")
    public void unlikeComment(@PathVariable long commentId) {
        likeService.unlikeComment(commentId);
    }
}
