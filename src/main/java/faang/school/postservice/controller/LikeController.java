package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeControllerValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/like")
public class LikeController {

    private final LikeControllerValidator validator;
    private final LikeService likeService;

    @PostMapping("/post")
    public void addLikeToPost(@Valid @RequestBody LikeDto likeDto) {
        validator.validAddLikeToPost(likeDto.getPostId());
        likeService.addLikeToPost(likeDto);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    public String deleteLikeFromPost(@Positive @PathVariable("postId") long postId,
                                   @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromPost(postId, userId);
        return "test";
    }

    @PostMapping("/comment")
    public void addLikeToComment(@Valid @RequestBody LikeDto likeDto) {
        validator.validAddLikeToComment(likeDto.getCommentId());
        likeService.addLikeToComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    public void deleteLikeFromComment(@Positive @PathVariable("commentId") long commentId,
                                      @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromComment(commentId, userId);
    }
}