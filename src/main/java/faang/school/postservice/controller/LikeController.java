package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/like")
@RestController
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/likes/post/{postId}")
    public List<UserDto> getPostLiker(
            @NotNull(message = "Post ID cannot be null")
            @Positive(message = "Post ID must be positive") @PathVariable long postId) {
        return likeService.getUsersWhoLikedPost(postId);
    }

    @GetMapping("/likes/comment/{commentId}")
    public List<UserDto> getCommentLiker(
            @NotNull(message = "Comment ID cannot be null")
            @Positive(message = "Comment ID must be positive") @PathVariable long commentId) {
        return likeService.getUsersWhoLikedComment(commentId);
    }

    @PostMapping("/user/{userId}/post")
    public ResponseEntity<Void> addLikeToPost(
            @Valid @RequestBody LikeDto likeDto,
            @NotNull(message = "User ID cannot be null")
            @Positive(message = "User ID must be positive") @PathVariable Long userId) {
        likeService.addLikeToPost(likeDto.getPostId(), likeDto.getCommentId(), userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/posts/{postId}/users/{userId}")
    public ResponseEntity<Void> removeLikeFromPost(
            @NotNull(message = "Post ID cannot be null")
            @Positive(message = "Post ID must be positive") @PathVariable Long postId,
            @NotNull(message = "User ID cannot be null")
            @Positive(message = "User ID must be positive") @PathVariable Long userId) {
        likeService.removeLikeFromPost(postId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/user/{userId}/comment")
    public ResponseEntity<Void> addLikeToComment(
            @Valid @RequestBody LikeDto likeDto,
            @NotNull(message = "User ID cannot be null")
            @Positive(message = "User ID must be positive") @PathVariable Long userId) {
        likeService.addLikeToComment(likeDto.getCommentId(), likeDto.getPostId(), userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/comments/{commentId}/users/{userId}")
    public ResponseEntity<Void> removeLikeFromComment(
            @NotNull(message = "Comment ID cannot be null")
            @Positive(message = "Comment ID must be positive") @PathVariable Long commentId,
            @NotNull(message = "User ID cannot be null")
            @Positive(message = "User ID must be positive") @PathVariable Long userId) {
        likeService.removeLikeFromComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
