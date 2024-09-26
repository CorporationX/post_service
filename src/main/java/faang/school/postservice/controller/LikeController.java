package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @GetMapping(value = "/users/post/{postId}")
    public List<UserDto> getAllUsersLikedPost(@PathVariable long postId) {
        return likeService.getAllUsersLikedPost(postId);
    }

    @GetMapping(value = "/users/comment/{commentId}")
    public List<UserDto> getAllUsersLikedComment(@PathVariable long commentId) {
        return likeService.getAllUsersLikedComment(commentId);
    }

}
