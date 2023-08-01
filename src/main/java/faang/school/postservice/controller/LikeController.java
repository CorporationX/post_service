package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}/like")
    public LikeDto addLikeToPost(@PathVariable long postId, @RequestBody LikeDto like) {
        return likeService.addLikeToPost(postId, like);
    }

    @PostMapping("comment/{commentId}/like")
    public LikeDto addLikeToComment(@PathVariable long commentId, @RequestBody LikeDto like) {
        return likeService.addLikeToComment(commentId, like);
    }

    @DeleteMapping("/user/{userId}/post/{postId}/like")
    public void deleteLikeFromPost(long userId, long postId) {

    }

    @DeleteMapping("/user/{userId}/comment/{commentId}/like")
    public void deleteLikeFromComment(long userId, long postId) {

    }
}
