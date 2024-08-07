package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("likes")
public class LikeController {
    private final LikeService likeService;
    private final UserService userService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersThatLikedPost(@PathVariable Long postId) {
        return likeService.getUsersThatLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersThatLikedComment(@PathVariable Long commentId) {
        return likeService.getUsersThatLikedComment(commentId);
    }

    //test
    @GetMapping("/users/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

}
