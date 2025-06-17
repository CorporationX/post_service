package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.LikeServiceInterface;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/likes")
@Slf4j
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final LikeServiceInterface likeServiceInterface;

    @GetMapping("posts/{postId}")
    public Page<UserDto> getListLikedByPost(@PathVariable @Min(1) Long postId,
                                            Pageable pageable) {
        return likeServiceInterface.findLikersByPostId(postId, pageable);
    }

    @GetMapping("/comments/{commentId}")
    public Page<UserDto> getListLikedByComment(@PathVariable @Min(1) Long commentId,
                                               Pageable pageable) {
        return likeServiceInterface.findLikersByCommentId(commentId, pageable);
    }

    @PostMapping("post/{postId}/user/{userId}")
    public LikeDto addLikeToPost(@PathVariable Long postId, @PathVariable Long userId) {
        log.info("Start method addLikeToPost with postId: {} and userId: {}",
                postId,
                userId);
        validateId(userId);
        validateId(postId);

        return likeService.addLikeToPost(userId, postId);
    }

    @DeleteMapping("post/{postId}/user/{userId}")
    public ResponseEntity<Void> removeLikeFromPost(@PathVariable Long postId, @PathVariable Long userId) {
        log.info("Start method removeLikeFromPost with postId: {}", postId);
        validateId(postId);
        validateId(userId);

        return likeService.removeLikeFromPost(postId, userId) ?
                new ResponseEntity<>(HttpStatus.NO_CONTENT) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("comment/{commentId}/user/{userId}")
    public LikeDto addLikeToComment(@PathVariable Long commentId, @PathVariable Long userId) {
        log.info("Start method addLikeToComment with commentId: {} and userId: {}", commentId, userId);
        validateId(userId);
        validateId(commentId);

        return likeService.addLikeToComment(userId, commentId);
    }

    @DeleteMapping("comment/{commentId}/user/{userId}")
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
