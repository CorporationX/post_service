package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("like/post/{postId}/user/{userId}")
    public LikeDto addLikeToPost(@PathVariable Long postId, @PathVariable Long userId) {
        log.info("Start method addLikeToPost with postId: {} and userId: {}", postId, userId);
        validateId(userId);
        validateId(postId);

        return likeService.addLikeToPost(userId, postId);
    }

    @DeleteMapping("like/post/{postId}/user/{userId}")
    public ResponseEntity<Void> removeLikeFromPost(@PathVariable Long postId, @PathVariable Long userId) {
        log.info("Start method removeLikeFromPost with postId: {}", postId);
        validateId(postId);
        validateId(userId);

        return likeService.removeLikeFromPost(postId, userId) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("like/comment/{commentId}/user/{userId}")
    public LikeDto addLikeToComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Start method addLikeToComment with commentId: {} and userId: {}", commentId, userId);
        validateId(userId);
        validateId(commentId);

        return likeService.addLikeToComment(userId, commentId);
    }

    @DeleteMapping("like/comment/{commentId}/user/{userId}")
    public ResponseEntity<Void> removeLikeFromComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Start method removeLikeFromComment with commentId: {}", commentId);
        validateId(commentId);
        validateId(userId);

        return likeService.removeLikeFromComment(commentId, userId) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private void validateId(Long id) {
        if (Objects.isNull(id) || id <= 0) {
            log.error("Used id is null or negative: {}", id);
            throw new DataValidationException("Negative or null id! Use valid Id!");
        }
        log.info("Used id {} was successfully validated!", id);
    }
}
