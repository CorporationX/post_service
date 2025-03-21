package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<LikeDto> likePost(@PathVariable("postId") long postId) {
        return null;
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable("postId") long postId) {
        return null;
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeDto> likeComment(@PathVariable("commentId") long commentId) {
        return null;
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(@PathVariable("commentId") long commentId) {
        return null;
    }
}
