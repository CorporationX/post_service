package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/create_for_post")
    public LikeDto makeLikeForPost (@RequestBody LikeDto likeDto) {
        if (likeDto.getPostId() == null) {
            throw new DataValidationException("PostId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            return likeService.createLikeForPost(likeDto);
        }
    }
    @DeleteMapping("/delete/{postId}")
    public void deleteLikeFromPost (@PathVariable long postId, @RequestBody LikeDto likeDto) {
        if (likeDto.getPostId() == null ) {
            throw new DataValidationException("PostId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            likeService.deleteLikeForPost(likeDto);
        }
    }

    @PostMapping("/create_for_comment")
    public LikeDto makeLikeForComment (@RequestBody LikeDto likeDto) {
        if (likeDto.getCommentId() == null ) {
            throw new DataValidationException("CommentId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            return likeService.createLikeForComment(likeDto);
        }
    }

    @DeleteMapping("/delete/{commentId}")
    public void deleteLikeFromComment (@PathVariable long commentId, @RequestBody LikeDto likeDto) {
        if (likeDto.getCommentId() == null ) {
            throw new DataValidationException("CommentId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            likeService.deleteLikeForComment(likeDto);
        }
    }
}
