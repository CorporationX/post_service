package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeValidator likeValidator;
    private final LikeService likeService;

    @PostMapping("posts/{postId}")
    public LikeDto addLikePost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeValidator.checkIsNull(postId, likeDto);
        return likeService.addLikePost(postId, likeDto);
    }

    @PostMapping("/comments/{commentId}")
    public LikeDto addLikeComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeValidator.checkIsNull(commentId, likeDto);
        return likeService.addLikeComment(commentId, likeDto);
    }

    @DeleteMapping("posts/{postId}")
    public LikeDto deleteLikePost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeValidator.checkIsNull(postId, likeDto);
        return likeService.deleteLikePost(postId, likeDto);
    }

    @DeleteMapping("comments/{commentId}")
    public LikeDto deleteLikeComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeValidator.checkIsNull(commentId, likeDto);
        return likeService.deleteLikeComment(commentId, likeDto);
    }
}