package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.service.post.LikeService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
@RequiredArgsConstructor
public class LikeController {

    @Autowired
    private LikeService likeService;

    @GetMapping("/like/post/{postId}")
    public List<UserDto> getUsersThatLikedThePost(@PathVariable long postId) {
        return likeService.getUsersThatLikedThePost(postId);
    }

    @GetMapping("/like/comment/{commentId}")
    public List<UserDto> getUsersThatLikedTheComment(
                                                @PathVariable long commentId) {
        return likeService.getUsersThatLikedTheComment(commentId);
    }

}
