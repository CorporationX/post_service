package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto createPostLike(@PathVariable Long postId, @Validated @RequestBody LikeDto dto) {
        var likeDto = new LikeDto(dto.userId(), null, postId);
        return likeService.createPostLike(likeDto);
    }

    @DeleteMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePostLike(@PathVariable Long postId, @Validated @RequestBody LikeDto dto) {
        likeService.removePostLike(dto);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto createCommentLike(@PathVariable Long commentId, @Validated @RequestBody LikeDto dto) {
        return likeService.createCommentLike(dto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentLike(@PathVariable Long commentId, @Validated @RequestBody LikeDto dto) {
        likeService.removeCommentLike(dto);
    }

    @GetMapping("/post/{postId}")
    public List<UserDto> getAllUsersWhoLikedPost(@PathVariable Long postId) {
        return likeService.getAllUsersWhoLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getAllUsersWhoLikedComment(@PathVariable Long commentId) {
        return likeService.getAllUsersWhoLikedComment(commentId);
    }
}
