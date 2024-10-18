package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/post/{postId}")
@RequiredArgsConstructor
public class LikeController {
    private static final long MIN_ID = 0L;

    private final LikeService service;

    @PutMapping("/like")
    public LikeDto addPostLike(
            @PathVariable @Min(MIN_ID) Long postId,
            @RequestBody @Valid LikeDto dto) {
        return service.addPostLike(postId, dto);
    }

    @DeleteMapping("/unlike")
    public void deletePostLike(
            @PathVariable @Min(MIN_ID) Long postId,
            @RequestBody @Valid LikeDto dto) {
        service.deletePostLike(postId, dto);
    }

    @PutMapping("/comment/{commentId}/like")
    public LikeDto addCommentLike(
            @PathVariable @Min(MIN_ID) Long postId,
            @PathVariable @Min(MIN_ID) Long commentId,
            @RequestBody @Valid LikeDto dto) {
        return service.addCommentLike(postId, commentId, dto);
    }

    @DeleteMapping("/comment/{commentId}/unlike")
    public void deleteCommentLike(
            @PathVariable @Min(MIN_ID) Long postId,
            @PathVariable @Min(MIN_ID) Long commentId,
            @RequestBody @Valid LikeDto dto) {
        service.deleteCommentLike(postId, commentId, dto);
    }
}
