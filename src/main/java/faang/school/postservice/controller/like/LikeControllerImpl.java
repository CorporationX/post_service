package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.facade.like.LikeFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/likes")
@AllArgsConstructor
public class LikeControllerImpl implements LikeController {
    private final LikeFacade likeFacade;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponseDto> addLikeToPost(@PathVariable long postId) {
        LikeResponseDto savedLike = likeFacade.addLikeToPost(postId);
        log.info("User with id = {} added a like to post with id = {}", savedLike.getUserId(), postId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedLike);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<LikeResponseDto> addLikeToComment(@PathVariable long commentId) {
        LikeResponseDto savedLike = likeFacade.addLikeToComment(commentId);
        log.info("User with id = {} added a like to comment with id = {}", savedLike.getUserId(), commentId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedLike);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteLikeFromPost(@PathVariable long postId) {
        likeFacade.deleteLikeFromPost(postId);
        log.info("Like deleted from post with id {}", postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteLikeFromComment(@PathVariable long commentId) {
        likeFacade.deleteLikeFromComment(commentId);
        log.info("Like deleted from comment with id {}", commentId);
        return ResponseEntity.noContent().build();
    }
}
