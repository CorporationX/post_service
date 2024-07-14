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
@RequestMapping("/liked")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getAllLikesPost(@PathVariable Long postId) {
        return likeService.getAllLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getAllLikesComment(@PathVariable Long commentId) {
        return likeService.getAllLikedComment(commentId);
    }
}
