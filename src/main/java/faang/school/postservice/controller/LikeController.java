package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
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
    @DeleteMapping("/delete_from_post")
    public LikeDto deleteLikeFromPost (@RequestBody LikeDto likeDto) {
        if (likeDto.getPostId() == null ) {
            throw new DataValidationException("PostId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            return likeService.deleteLikeForPost(likeDto);
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

    @DeleteMapping("/delete_from_comment")
    public LikeDto deleteLikeFromComment (@RequestBody LikeDto likeDto) {
        if (likeDto.getCommentId() == null ) {
            throw new DataValidationException("CommentId cannot be empty");
        } else if  (likeDto.getUserId() == null) {
            throw new DataValidationException("UserId cannot be empty");
        } else {
            return likeService.deleteLikeForComment(likeDto);
        }
    }
}
