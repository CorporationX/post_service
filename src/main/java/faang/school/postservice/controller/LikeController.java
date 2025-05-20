package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Example API", description = "API for Post Service")
@AllArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    // добавление нового поста пользователя
    @PostMapping("/post")
    @Operation(summary = "Add user post", description = "Add user post")
    public LikePostResponseDto addPost(
        @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("add post request: {}", likePostRequestDto);
        return likeService.addPost(likePostRequestDto);
    }

    // удаление поста пользователя
    @DeleteMapping("/post")
    @Operation(summary = "Delete user post", description = "Delete user post")
    public LikePostResponseDto deletePost(
        @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("delete post request: {}", likePostRequestDto);
        return likeService.deletePost(likePostRequestDto);
    }

    // добавление нового комментария пользователя
    @PostMapping("/comment")
    @Operation(summary = "Add new user comment", description = "Add new user comment")
    public LikeCommentResponseDto addComment(
        @Valid @RequestBody LikeCommentRequestDto likeCommentDto
    ) {
        log.debug("add comment request: {}", likeCommentDto);
        return likeService.addComment(likeCommentDto);
    }

    // удаление комментария пользователя
    @DeleteMapping("/comment")
    @Operation(summary = "Delete user comment", description = "Delete user comment")
    public LikeCommentResponseDto deleteComment(
        @Valid @RequestBody LikeCommentRequestDto likeCommentDto
    ) {
        log.debug("delete comment request: {}", likeCommentDto);
        return likeService.deleteComment(likeCommentDto);
    }
}
