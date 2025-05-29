package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Post Service API", description = "API for Post Service")
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;
    private final UserContext userContext;
    private final LikeMapper likeMapper;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add user like to post", description = "Add user like to post")
    public LikePostResponseDto addPost(
            @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("add like to post request: {}", likePostRequestDto);
        LikeDto likeDto = getLikeDto(likePostRequestDto);
        LikeDto resultLikeDto = likeService.addPost(likeDto);
        return likeMapper.toPostResponseDto(resultLikeDto);
    }

    @DeleteMapping("/post")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user like from post", description = "Delete user like from post")
    public void deletePost(
            @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("delete like from post request: {}", likePostRequestDto);
        LikeDto likeDto = getLikeDto(likePostRequestDto);
        likeService.deletePost(likeDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add user like to comment", description = "Add user like to comment")
    public LikeCommentResponseDto addComment(
            @Valid @RequestBody LikeCommentRequestDto likeCommentRequestDto
    ) {
        log.debug("add like to comment request: {}", likeCommentRequestDto);
        LikeDto likeDto = getLikeDto(likeCommentRequestDto);
        LikeDto resultLikeDto = likeService.addComment(likeDto);
        return likeMapper.toCommentResponseDto(resultLikeDto);
    }

    @DeleteMapping("/comment")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user like from comment", description = "Delete user like from comment")
    public void deleteComment(
            @Valid @RequestBody LikeCommentRequestDto likeCommentRequestDto
    ) {
        log.debug("delete like from comment request: {}", likeCommentRequestDto);
        LikeDto likeDto = getLikeDto(likeCommentRequestDto);
        likeService.deleteComment(likeDto);
    }

    private LikeDto getLikeDto(LikePostRequestDto likePostRequestDto) {
        return LikeDto.builder()
                .userId(userContext.getUserId())
                .postId(likePostRequestDto.postId())
                .build();
    }

    private LikeDto getLikeDto(LikeCommentRequestDto likeCommentRequestDto) {
        return LikeDto.builder()
                .userId(userContext.getUserId())
                .commentId(likeCommentRequestDto.commentId())
                .build();
    }
}
