package faang.school.postservice.controller;


import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/likes")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post")
    public LikeDto likePost(@RequestBody @Validated LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @PostMapping("/comment")
    public LikeDto likeComment(@RequestBody @Validated LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }


    @DeleteMapping("/post/{postId}")
    public void unlikePost(@PathVariable @Positive Long postId) {
        likeService.deleteLikePost(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void unlikeComment(@PathVariable @Positive Long commentId) {
        likeService.deleteLikeComment(commentId);
    }


}
