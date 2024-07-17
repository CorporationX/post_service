package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeControllerValidator validator;
    private final LikeService likeService;

    @PutMapping
    public void addLikeToPost(@RequestParam("userId") long userId, @RequestParam("id") long postId) {
        validator.validAddLikeToPost(userId, postId);
        likeService.addLikeToPost(userId, postId);
    }

    @DeleteMapping
    public void deleteLikeFromPost(@RequestBody LikeDto likeDto) {
        validator.validDeleteLikeFromPost(likeDto);
        likeService.deleteLikeFromPost(likeDto);
    }

    @PutMapping
    public void addLikeTOComment(@RequestParam("userId") long userId, @RequestParam("id") long commentId) {
        validator.validAddLikeTOComment(userId, commentId);
        likeService.addLikeTOComment(userId, commentId);
    }

    @DeleteMapping
    public void deleteLikeFromComment(@RequestBody LikeDto likeDto) {
        validator.validDeleteLikeFromComment(likeDto);
        likeService.deleteLikeFromComment(likeDto);
    }

    @GetMapping
    public long getCountLikeForPost(@RequestParam("id") long postId) {
        validator.validGetCountLikeForPost(postId);
        return likeService.getCountLikeForPost(postId);
    }
}
