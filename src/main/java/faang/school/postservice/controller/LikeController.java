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

    @PostMapping("/add/topost")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToPost(@RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToPost(fakeLike);

        return likeMapper.toDto(like);
    }

    @PostMapping("/add/tocomment")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToComment(@RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToComment(fakeLike);

        return likeMapper.toDto(like);
    }

    @DeleteMapping("/remove/{likeId}")
    @ResponseStatus(HttpStatus.OK)
    void deletePostLike(@NonNull @PathVariable Long likeId, @RequestParam Long postId) {
        likeService.deletePostLike(likeId, postId);
    }

    @DeleteMapping("/remove/{likeId}")
    @ResponseStatus(HttpStatus.OK)
    void deleteCommentLike(@NonNull @PathVariable Long likeId, @RequestParam Long commentId) {
        likeService.deleteCommentLike(likeId, commentId);
    }
}
