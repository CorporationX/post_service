package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/post/{postId}/users")
    public List<UserDto> getUsersWhoLikedPost(@PathVariable Long postId) {
        log.debug("Received request to get users who liked post with id: {}", postId);
        List<UserDto> users = likeService.getUsersWhoLikedPost(postId);
        log.info("Received users {} who liked post with id: {}", users, postId);
        return users;
    }

    @GetMapping("/comments/{commentId}/users")
    public List<UserDto> getUsersWhoLikedComment(@PathVariable Long commentId) {
        log.debug("Received request to get users who liked comment with id: {}", commentId);
        List<UserDto> users = likeService.getUsersWhoLikedComment(commentId);
        log.info("Returned {} users who liked comment with id: {}", users.size(), commentId);
        return users;
    }

    @PostMapping("/post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeForPost(@PathVariable("id") Long postId) {
        likeService.createLikeForPost(postId);
    }

    @PostMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeForComment(@PathVariable("id") Long commentId) {
        likeService.createLikeForComment(commentId);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@PathVariable @Positive Long postId) {
        likeService.deleteLikeFromPost(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@PathVariable @Positive Long commentId) {
        likeService.deleteLikeFromComment(commentId);
    }
}