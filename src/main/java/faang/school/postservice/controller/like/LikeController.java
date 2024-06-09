package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
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
    public List<UserDto> getUsersLikedPostByPostId(@PathVariable long postId) {
        return likeService.getUsersLikedPostByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikedCommentByCommentId(@PathVariable long commentId) {
        return likeService.getUsersLikedCommentByCommentId(commentId);
    }
}
