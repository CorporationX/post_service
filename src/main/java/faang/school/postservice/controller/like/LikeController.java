package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto addLikeToPost(@PathVariable long postId) {
        return likeService.addLikeToPost(postId);
    }

    @DeleteMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromPost(@PathVariable long postId) {
        likeService.removeLikeFromPost(postId);
    }

    @PostMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto addLikeToComment(@PathVariable long commentId) {
        return likeService.addLikeToComment(commentId);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLikeFromComment(@PathVariable long commentId) {
        likeService.removeLikeFromComment(commentId);
    }

    @GetMapping("/posts/{postId}/likes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeDto> getLikesFromPost(@PathVariable long postId) {
        return likeService.getLikesFromPost(postId);
    }
}
