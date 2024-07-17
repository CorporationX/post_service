package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "${spring.mvc.servlet.path}/like")
@AllArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/comment")
    public LikeDto likeComment(@RequestBody LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    public void deleteLikeFromComment(@PathVariable long commentId, @PathVariable  long userId) {
        likeService.deleteLikeFromComment(commentId, userId);
    }

    @PostMapping("/post")
    public LikeDto likePost(@RequestBody LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    public void deleteLikeFromPost(@PathVariable long postId, @PathVariable  long userId) {
        likeService.deleteLikeFromPost(postId, userId);
    }
}