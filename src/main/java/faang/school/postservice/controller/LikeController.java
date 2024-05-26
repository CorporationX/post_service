package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    @Operation(summary = "Like post")
    public LikeDto likePost(@RequestBody @Validated LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/post/{postId}")
    @Operation(summary = "Remove like from post")
    public void deleteLikeFromPost(@Valid @PathVariable Long postId) {
        likeService.deleteLikeFromPost(postId);
    }

    @PostMapping("/comment/{commentId}")
    @Operation(summary = "Like comment")
    public LikeDto likeComment(@RequestBody @Validated LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "Remove like from comment")
    public void deleteLikeFromComment(@Valid @PathVariable Long commentId) {
        likeService.deleteLikeFromComment(commentId);
    }
}