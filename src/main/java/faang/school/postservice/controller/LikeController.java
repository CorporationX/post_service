package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponseDto> likePost(@PathVariable long postId,
                                                    @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.likePost(postId, likeRequestDto));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<LikeResponseDto> removeLikeFromPost(@PathVariable long postId,
                                                              @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.removeLikeFromPost(postId, likeRequestDto));
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<LikeResponseDto> likeComment(@PathVariable long commentId,
                                                       @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.likeComment(commentId, likeRequestDto));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<LikeResponseDto> removeLikeFromComment(@PathVariable long commentId,
                                                                 @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.removeLikeFromComment(commentId, likeRequestDto));
    }

    @GetMapping("/post/{postId}/users")
    public ResponseEntity<List<UserDto>> getUsersWhoLikedAPost(@PathVariable long postId) {
        return ResponseEntity.ok(likeService.getUsersWhoLikedPost(postId));
    }

    @GetMapping("/comment/{commentId}/users")
    public ResponseEntity<List<UserDto>> getUsersWhoLikedAComment(@PathVariable long commentId) {
        return ResponseEntity.ok(likeService.getUsersWhoLikedComment(commentId));
    }
}
