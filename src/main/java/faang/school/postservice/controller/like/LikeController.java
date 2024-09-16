package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;
  
    @PostMapping("/post")
    public void likePost(@RequestBody LikeDto likeDto) {
        likeService.likePost(likeDto);
    }

    @DeleteMapping("/post")
    public void unlikePost(@RequestBody LikeDto likeDto) {
        likeService.unlikePost(likeDto);
    }

    @PostMapping("/comment")
    public void likeComment(@RequestBody LikeDto likeDto) {
        likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment")
    public void unlikeComment(@RequestBody LikeDto likeDto) {
        likeService.unlikeComment(likeDto);
    }

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersThatLikedPost(@PathVariable Long postId) {
        return likeService.getUsersThatLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersThatLikedComment(@PathVariable Long commentId) {
        return likeService.getUsersThatLikedComment(commentId);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    public UserDto deleteLikeFromPost(@PathVariable Long postId, @PathVariable Long userId) {
        return likeService.deleteLikeFromPost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    public UserDto deleteLikeFromComment(@PathVariable Long commentId, @PathVariable Long userId) {
        return likeService.deleteLikeFromComment(commentId, userId);
    }
}
