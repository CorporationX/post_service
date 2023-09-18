package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    private final LikeValidator likeValidator;

    @PostMapping("/create/on_post")
    public LikeDto makeLikeForPost(@RequestBody LikeDto likeDto) {
        likeValidator.validatePostId(likeDto);
        likeValidator.validateUserId(likeDto);
        return likeService.createLikeForPost(likeDto);
    }

    @DeleteMapping("/delete/from_post")
    public LikeDto deleteLikeFromPost(@RequestBody LikeDto likeDto) {
        likeValidator.validatePostId(likeDto);
        likeValidator.validateUserId(likeDto);
        return likeService.deleteLikeForPost(likeDto);
    }

    @PostMapping("/create/on_comment")
    public LikeDto makeLikeForComment(@RequestBody LikeDto likeDto) {
        likeValidator.validateCommentId(likeDto);
        likeValidator.validateUserId(likeDto);
        return likeService.createLikeForComment(likeDto);
    }

    @DeleteMapping("/delete/from_comment")
    public LikeDto deleteLikeFromComment(@RequestBody LikeDto likeDto) {
        likeValidator.validateCommentId(likeDto);
        likeValidator.validateUserId(likeDto);
        return likeService.deleteLikeForComment(likeDto);
    }
}
