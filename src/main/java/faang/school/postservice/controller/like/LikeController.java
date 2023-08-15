package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
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
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public LikeDto likePost (@Valid @RequestBody LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/post/{postId}/user/{userId}")
    public void deleteLikePost(@PathVariable Long postId, @PathVariable Long userId) {
        likeService.deleteLikePost(postId, userId);
    }
}
