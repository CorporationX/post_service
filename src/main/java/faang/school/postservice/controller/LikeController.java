package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.dto.like.TestLikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public LikePostResponseDto addLikeToPost(
            @Valid @RequestBody LikePostRequestDto likePostRequestDto
    ) {
        log.debug("add like to post request: {}", likePostRequestDto);
        LikeDto likeDto = getLikeDto(likePostRequestDto);
        LikeDto resultLikeDto = likeService.addLikeToPost(likeDto);
        return likeMapper.toPostResponseDto(resultLikeDto);
    }

    @DeleteMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user like from post", description = "Delete user like from post")
    public void deleteLikeFromPost(@PathVariable Long postId) {
        log.debug("delete like from post. postId={}", postId);
        LikeDto likeDto = getPostLikeDto(postId);
        likeService.deleteLikeFromPost(likeDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add user like to comment", description = "Add user like to comment")
    public LikeCommentResponseDto addLikeToComment(
            @Valid @RequestBody LikeCommentRequestDto likeCommentRequestDto
    ) {
        log.debug("add like to comment request: {}", likeCommentRequestDto);
        LikeDto likeDto = getLikeDto(likeCommentRequestDto);
        LikeDto resultLikeDto = likeService.addLikeToComment(likeDto);
        return likeMapper.toCommentResponseDto(resultLikeDto);
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user like from comment", description = "Delete user like from comment")
    public void deleteLikeFromComment(@PathVariable Long commentId) {
        log.debug("delete like from comment. commentId: {}", commentId);
        LikeDto likeDto = getCommentLikeDto(commentId);
        likeService.deleteLikeFromComment(likeDto);
    }

    /**
     * Как только в сервисе появятся dto с несколькими обязательными параметрами, то этот метод можно будет удалить.
     * Он нужен для тестирования MethodArgumentNotValidException
     */
    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public void testPostDto(@Valid @RequestBody TestLikeDto testLikeDto) {
        log.debug("test TestLikeDto: {}", testLikeDto);
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

    private LikeDto getPostLikeDto(Long postId) {
        return LikeDto.builder()
                .userId(userContext.getUserId())
                .postId(postId)
                .build();
    }

    private LikeDto getCommentLikeDto(Long commentId) {
        return LikeDto.builder()
                .userId(userContext.getUserId())
                .commentId(commentId)
                .build();
    }
}
