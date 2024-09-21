package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/likePost")
    public LikeDto likeToPost(@RequestBody LikeDto likeDto) {
        return likeService.likeToPost(likeDto);
    }

    @DeleteMapping("/post/unlikePost")
    public void unlikeFromPost(@RequestBody LikeDto likeDto) {
        likeService.unlikeFromPost(likeDto);
    }

    @PostMapping("/comment/likeComment")
    public LikeDto likeToComment(@RequestBody LikeDto likeDto) {
        return likeService.likeToComment(likeDto);
    }

    @DeleteMapping("/comment/unlikeComment")
    public void unlikeFromComment(@RequestBody LikeDto likeDto) {
        likeService.unlikeFromComment(likeDto);
    }
}
