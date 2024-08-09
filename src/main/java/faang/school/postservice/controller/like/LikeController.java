package faang.school.postservice.controller.like;

import faang.school.postservice.controller.LikeToComment;
import faang.school.postservice.controller.LikeToPost;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/like")
@RequiredArgsConstructor
@Validated
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

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto addLikeToPost(@Validated(LikeToPost.class) @RequestBody LikeDto likeDto) {
        return likeService.addLikeToPost(likeDto);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeFromPost(@Positive @PathVariable("postId") long postId,
                                   @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromPost(postId, userId);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto addLikeToComment(@Validated(LikeToComment.class) @RequestBody LikeDto likeDto) {
        return likeService.addLikeToComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeFromComment(@Positive @PathVariable("commentId") long commentId,
                                      @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromComment(commentId, userId);
    }
}