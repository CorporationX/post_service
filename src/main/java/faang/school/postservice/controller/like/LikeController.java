package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getLikesUsersByPostId(@PathVariable long postId) {
        return likeService.getLikesUsersByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getLikesUsersByCommentId(@PathVariable long commentId) {
        return likeService.getLikesUsersByCommentId(commentId);
    }
}
