package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post")
    public void likePost(@RequestBody LikeDto likeDto) {
        likeService.likePost(likeDto);
    }

    @DeleteMapping("/post")
    public void unlikePost(@RequestBody LikeDto likeDto) {
        likeService.unlikePost(likeDto);
    }

    @PostMapping("/comment")
    public void likeComment(@RequestBody LikeDto likeDto) {
        likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment")
    public void unlikeComment(@RequestBody LikeDto likeDto) {
        likeService.unlikeComment(likeDto);
    }
}
