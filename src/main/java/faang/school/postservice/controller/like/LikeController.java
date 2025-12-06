package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.util.like.LikeValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1")
@Slf4j
public class LikeController {

    private final LikeService likeService;
    private final LikeValidator likeValidator;

    @PostMapping("/posts/{postId}/likes")
    public LikeDto addLikeToPost(@PathVariable long postId, @Valid @RequestBody LikeDto likeDto) {
        likeValidator.validateLike(postId, likeDto);
        return likeService.addLikeToPost(postId, likeDto);
    }

    @DeleteMapping("/posts/{postId}/likes")
    public void removeLikeFromPost(@PathVariable long postId) {
        likeService.removeLikeFromPost(postId);
    }

    @PostMapping("/comments/{commentId}/likes")
    public LikeDto addLikeToComment(@PathVariable long commentId, @Valid @RequestBody LikeDto likeDto) {
        likeValidator.validateLike(commentId, likeDto);
        return likeService.addLikeToComment(commentId, likeDto);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public void removeLikeFromComment(@PathVariable long commentId) {
        likeService.removeLikeFromComment(commentId);
    }

    @GetMapping("/posts/{postId}/likes")
    public List<LikeDto> getLikesFromPost(@PathVariable long postId) {
        return likeService.getLikesFromPost(postId);
    }
}
