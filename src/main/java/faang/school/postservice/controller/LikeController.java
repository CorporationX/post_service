package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{postId}/post/users")
    public ResponseEntity<List<UserDto>> getUsersWhoLikedAPost(@PathVariable long postId) {
        return ResponseEntity.ok(likeService.getUsersWhoLikedPost(postId));
    }

    @GetMapping("/{commentId}/comment/users")
    public ResponseEntity<List<UserDto>> getUsersWhoLikedAComment(@PathVariable long commentId) {
        return ResponseEntity.ok(likeService.getUsersWhoLikedComment(commentId));
    }
}
