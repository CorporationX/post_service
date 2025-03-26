package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("posts/{postId}/likes")
    public List<UserDto> findAllUserWhoLikedPost(@PathVariable Long postId) {
        return likeService.findAllUserWhoLikedPost(postId);
    }

    @GetMapping("comments/{commentId}/likes")
    public List<UserDto> findAllUserWhoLikedComment(@PathVariable Long commentId) {
        return likeService.findAllUserWhoLikedComment(commentId);
    }

}
