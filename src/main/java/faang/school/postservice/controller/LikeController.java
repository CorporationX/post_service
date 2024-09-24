package faang.school.postservice.controller;

import faang.school.postservice.LikeMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeMapper likeMapper;
    private final LikeService likeService;

    @PostMapping("/add/post/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToPost(@NonNull @PathVariable Long postId, @RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToPost(postId, fakeLike);

        return likeMapper.toDto(like);
    }

    @PostMapping("/add/comment/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToComment(@NonNull @PathVariable Long commentId, @RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToComment(commentId, fakeLike);

        return likeMapper.toDto(like);
    }

    @DeleteMapping("/remove/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    void deletePostLike(@NonNull @PathVariable Long postId, @RequestParam Long userId) {
        likeService.deletePostLike(postId, userId);
    }

    @DeleteMapping("/remove/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteCommentLike(@NonNull @PathVariable Long commentId, @RequestParam Long userId) {
        likeService.deleteCommentLike(commentId, userId);
    }

    @GetMapping("/all/{postId}")
    int getLikesByPost(@NonNull @PathVariable Long postId) {
        return likeService.getLikesByPost(postId);
    }
}
