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
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("post/{postId}/users")
    public List<UserDto> getUsersLikedPost(@PathVariable long postId) {
        return likeService.getUsersLikedPost(postId);
    }

    @GetMapping("comment/{commentId}/users")
    public List<UserDto> getUsersLikedComment(@PathVariable long commentId) {
        return likeService.getUsersLikedComment(commentId);
    }
}
