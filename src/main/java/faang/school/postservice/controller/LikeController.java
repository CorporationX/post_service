package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exeption.DataValidationException;
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
        validateAddLikeToContent(postId);
        return likeService.addLikeToPost(postId, like);
    }

    @PostMapping("comment/{commentId}/like")
    public LikeDto addLikeToComment(@PathVariable long commentId, @RequestBody LikeDto like) {
        validateAddLikeToContent(commentId);
        return likeService.addLikeToComment(commentId, like);
    }

    @DeleteMapping("/user/{userId}/post/{postId}/like")
    public void deleteLikeFromPost(long postId, long userId) {
        validateAddLikeToContent(postId, userId);
        likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}/like")
    public void deleteLikeFromComment(long commentId, long userId) {
        validateAddLikeToContent(commentId, userId);
        likeService.deleteLikeFromComment(commentId, userId);
    }

    public void validateAddLikeToContent(long contentId){
        if(contentId <= 0){
            throw new DataValidationException("Id cannot be less than 1 !");
        }
    }
    public void validateAddLikeToContent(long contentId, long anotherContentId){
        if(contentId <= 0 || anotherContentId <= 0){
            throw new DataValidationException("Id cannot be less than 1 !");
        }
    }
}
