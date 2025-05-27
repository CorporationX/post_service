package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Post Service API", description = "API for Post Service")
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post")
    @Operation(summary = "Add user like to post", description = "Add user like to post")
    public LikePostResponseDto addPost(
            @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("add like to post request: {}", likePostRequestDto);
        return likeService.addPost(likePostRequestDto);
    }

    @DeleteMapping("/post")
    @Operation(summary = "Delete user like from post", description = "Delete user like from post")
    public LikePostResponseDto deletePost(
            @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("delete like from post request: {}", likePostRequestDto);
        return likeService.deletePost(likePostRequestDto);
    }

    @PostMapping("/comment")
    @Operation(summary = "Add user like to comment", description = "Add user like to comment")
    public LikeCommentResponseDto addComment(
            @Valid @RequestBody LikeCommentRequestDto likeCommentDto
    ) {
        log.debug("add like to comment request: {}", likeCommentDto);
        return likeService.addComment(likeCommentDto);
    }

    @DeleteMapping("/comment")
    @Operation(summary = "Delete user like from comment", description = "Delete user like from comment")
    public LikeCommentResponseDto deleteComment(
            @Valid @RequestBody LikeCommentRequestDto likeCommentDto
    ) {
        log.debug("delete like from comment request: {}", likeCommentDto);
        return likeService.deleteComment(likeCommentDto);
    }
}
