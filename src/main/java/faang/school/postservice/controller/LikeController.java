package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getPostLikeUsers(@NonNull @PathVariable("postId") Long postId) {
        return likeService.getUsersWhoLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getCommentLikeUsers(@NonNull @PathVariable("commentId") Long commentId) {
        return likeService.getUsersWhoLikedComment(commentId);
    }
}
