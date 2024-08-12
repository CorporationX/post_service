package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.ControllerValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post/{postId}")
@RequiredArgsConstructor
public class LikeController {
    private static final String MESSAGE_INVALID_COMMENT_ID = "Invalid commentId";
    private static final String MESSAGE_INVALID_POST_ID = "Invalid postId";

    private final ControllerValidator validator;
    private final LikeService service;

    @PutMapping("/like")
    public LikeDto addPostLike(@PathVariable Long postId, @RequestBody LikeDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateDto(dto);
        return service.addPostLike(postId, dto);
    }

    @DeleteMapping("/unlike")
    public LikeDto deletePostLike(@PathVariable Long postId, @RequestBody LikeDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateDto(dto);
        return service.deletePostLike(postId, dto);
    }

    @PutMapping("/comment/{commentId}/like")
    public LikeDto addCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody LikeDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateId(commentId, MESSAGE_INVALID_COMMENT_ID);
        validator.validateDto(dto);
        return service.addCommentLike(postId, commentId, dto);
    }

    @DeleteMapping("/comment/{commentId}/unlike")
    public LikeDto deleteCommentLike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody LikeDto dto) {
        validator.validateId(postId, MESSAGE_INVALID_POST_ID);
        validator.validateId(commentId, MESSAGE_INVALID_COMMENT_ID);
        validator.validateDto(dto);
        return service.deleteCommentLike(postId, commentId, dto);
    }


}
