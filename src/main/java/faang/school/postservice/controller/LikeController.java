package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
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
    public LikeDto likePost(@RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getPostId() == null) {
            throw new DataValidationException("Post id is required");
        }
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/user/{userId}/post/{postId}")
    public void unlikePost(@PathVariable long userId, @PathVariable long postId) {
        likeService.unlikePost(postId, userId);
    }

    @PostMapping("/comment")
    public LikeDto likeComment(@RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getCommentId() == null) {
            throw new DataValidationException("Comment id is required");
        }
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/user/{userId}/comment/{commentId}")
    public void unlikeComment(@PathVariable long userId, @PathVariable long commentId) {
        likeService.unlikeComment(commentId, userId);
    }

}
