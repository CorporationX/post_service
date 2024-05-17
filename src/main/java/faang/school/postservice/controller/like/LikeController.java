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
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class LikeController {

    private final LikeService likeService;


    @GetMapping("/like/{postId}")
    public List<UserDto> getUsersWhoLikedPost(@PathVariable Long postId) {
        return likeService.getAllUsersDtoByPostId(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersWhoLikedComment(@PathVariable Long commentId) {
        return likeService.getAllUsersDtoByCommentId(commentId);
    }
}
