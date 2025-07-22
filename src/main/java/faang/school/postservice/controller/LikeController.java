package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.likesystem.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {
    private final UserContext userContext;
    private final LikeService likeSystemService;

    @PostMapping(path = "/post/{postId}")
    public LikeDto addLikePost(@PathVariable Long postId) {
        return likeSystemService.addLikePost(postId, userContext.getUserId());
    }

    @DeleteMapping(path = "/post/{id}")
    public LikeDto deleteLikePost(@PathVariable Long id) {
        return likeSystemService.deleteLikePost(id);
    }

    @PostMapping(path = "/comment/{commentId}")
    public LikeDto addLikeComment(@PathVariable Long commentId) {
        return likeSystemService.addLikeComment(commentId, userContext.getUserId());
    }

    @DeleteMapping(path = "/comment/{id}")
    public LikeDto deleteLikeComment(@PathVariable Long id) {
        return likeSystemService.deleteLikeComment(id);
    }

    @GetMapping("/post/{postId}/users")
    public List<UserDto> getUsersWhoLikedPost(@PathVariable Long postId) {
        return likeSystemService.getUsersWhoLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}/users")
    public List<UserDto> getUsersWhoLikedComment(@PathVariable Long commentId) {
        return likeSystemService.getUsersWhoLikedComment(commentId);
    }
}
