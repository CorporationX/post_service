package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/like")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likePost(@RequestBody @Valid LikeDto likeDto) {
        return likeService.createLikeOnPost(likeDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likeComment(@RequestBody @Valid LikeDto likeDto) {
        return likeService.createLikeOnComment(likeDto);
    }

    @DeleteMapping("/deleteLikePost")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnPost(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteLikeOnPost(likeDto);
    }

    @DeleteMapping("/deleteLikeComment")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnComment(@RequestBody @Valid LikeDto likeDto) {
        likeService.deleteLikeOnComment(likeDto);
    }

    @GetMapping("/getLikes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeDto> getAllPostLikes(@RequestBody @Valid LikeDto likeDto) {
        return likeService.getAllPostLikes(likeDto);
    }

    @GetMapping("/{postId}/get-users-by-post")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsersFromPost(@PathVariable @Valid long postId) {
        return likeService.getUsersLikeFromPost(postId);
    }

    @GetMapping("/{commentId}/getAllUsers")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsersFromComment(@PathVariable @Valid long commentId) {
        return likeService.getUsersLikeFromComment(commentId);
    }
}
