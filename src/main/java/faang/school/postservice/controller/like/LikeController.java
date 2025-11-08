package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/likes")
@RestController
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    public ResponseEntity<LikeDto> addLikeToPost(@RequestParam @Positive long postId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.addLikeToPost(postId));
    }

    @DeleteMapping("/post")
    public ResponseEntity<LikeDto> removeLikeFromPost(@RequestParam @Positive long postId) {
        return ResponseEntity.ok(likeService.removeLikeFromPost(postId));
    }

    @PostMapping("/comment")
    public ResponseEntity<LikeDto> addLikeToComment(@RequestParam @Positive long commentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(likeService.addLikeToComment(commentId));
    }

    @DeleteMapping("/comment")
    public ResponseEntity<LikeDto> removeLikeFromComment(@RequestParam @Positive long commentId) {
        return ResponseEntity.ok(likeService.removeLikeFromComment(commentId));
    }
}