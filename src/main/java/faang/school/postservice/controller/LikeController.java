package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validation.LikeControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeControllerValidator likeControllerValidator;
    private final LikeService likeService;

    @PostMapping("/like/post/{postId}")
    public LikeDto addLikeToPost(@PathVariable long postId, @RequestBody LikeDto like) {
        likeControllerValidator.validate(postId);
        return likeService.addLikeToPost(postId, like);
    }

    @PostMapping("/like/comment/{commentId}")
    public LikeDto addLikeToComment(@PathVariable long commentId, @RequestBody LikeDto like) {
        likeControllerValidator.validate(commentId);
        return likeService.addLikeToComment(commentId, like);
    }

    @DeleteMapping("/like/user/{userId}/post/{postId}")
    public void deleteLikeFromPost(long postId, long userId) {
        likeControllerValidator.validate(postId, userId);
        likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/like/user/{userId}/comment/{commentId}")
    public void deleteLikeFromComment(long commentId, long userId) {
        likeControllerValidator.validate(commentId, userId);
        likeService.deleteLikeFromComment(commentId, userId);
    }
}
