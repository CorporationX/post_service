package faang.school.postservice.controller;

import faang.school.postservice.dto.likes.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;
    private final LikeValidator likeValidator;

    private static final String POSTS_PATH = "/posts";
    private static final String COMMENTS_PATH = "/comments";
    private static final String POST_ID_PATH = "/{postId}";
    private static final String COMMENT_ID_PATH = "/{commentId}";
    private static final String USER_ID_PATH = "/{userId}";
    private static final String DELETE_POST_LIKE_PATH = POSTS_PATH + USER_ID_PATH + POST_ID_PATH;
    private static final String DELETE_COMMENT_LIKE_PATH = COMMENTS_PATH + USER_ID_PATH + COMMENT_ID_PATH;

    @PostMapping(POSTS_PATH)
    public ResponseEntity<LikeDto> likePost(@RequestParam Long userId, @RequestParam Long postId) {
        likeValidator.validateUser(userId);
        if (postId == null) {
            throw new DataValidationException("PostId не может быть пустым");
        }
        return new ResponseEntity<>(likeService.likePost(userId, postId), HttpStatus.CREATED);
    }

    @DeleteMapping(DELETE_POST_LIKE_PATH)
    public ResponseEntity<String> deletePostLike(@PathVariable long userId, @PathVariable long postId) {
        likeService.deletePostLike(userId, postId);
        return ResponseEntity.ok("Лайк успешно удален!");
    }

    @PostMapping(COMMENTS_PATH)
    public ResponseEntity<LikeDto> likeComment(@RequestParam Long userId, @RequestParam Long commentId) {
        likeValidator.validateUser(userId);
        if (commentId == null) {
            throw new DataValidationException("CommentId не может быть пустым");
        }
        return new ResponseEntity<>(likeService.likeComment(userId, commentId), HttpStatus.CREATED);
    }

    @DeleteMapping(DELETE_COMMENT_LIKE_PATH)
    public ResponseEntity<String> deleteCommentLike(@PathVariable long userId, @PathVariable long commentId) {
        likeService.deleteCommentLike(userId, commentId);
        return ResponseEntity.ok("Лайк успешно удален!");
    }
}
