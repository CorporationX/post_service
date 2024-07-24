package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersByPostId(@PathVariable Long postId) {
        return likeService.findUsersByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersByCommentId(@PathVariable Long commentId) {
        return likeService.findUsersByCommentId(commentId);
    }
}
