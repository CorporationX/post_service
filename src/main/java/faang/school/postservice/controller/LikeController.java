package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
@Validated
public class LikeController {
    private final LikeService likeService;

    @PostMapping("posts/{id}")
    public LikeDto addLikePost(@PathVariable Long id, @Valid @RequestBody LikeDto likeDto) {
        return likeService.addLikePost(id, likeDto);
    }

    @PostMapping("/comments/{id}")
    public LikeDto addLikeComment(@PathVariable Long id, @Valid @RequestBody LikeDto likeDto) {
        return likeService.addLikeComment(id, likeDto);
    }

    @DeleteMapping("posts/{id}")
    public LikeDto deleteLikePost(@PathVariable Long id, @Valid @RequestBody LikeDto likeDto) {
        return likeService.deleteLikePost(id, likeDto);
    }

    @DeleteMapping("comments/{id}")
    public LikeDto deleteLikeComment(@PathVariable Long id, @Valid @RequestBody LikeDto likeDto) {
        return likeService.deleteLikeComment(id, likeDto);
    }
}