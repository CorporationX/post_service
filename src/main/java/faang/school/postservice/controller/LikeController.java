package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeControllerValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {

    private final LikeControllerValidator validator;
    private final LikeService likeService;

    @PostMapping("/post")
    public ResponseEntity<LikeDto> addLikeToPost(@Valid @RequestBody LikeDto likeDto) {
        validator.validAddLikeToPost(likeDto.getPostId());
        LikeDto createLike = likeService.addLikeToPost(likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createLike);
    }

    @DeleteMapping("/post/{postId}/{userId}")
    public ResponseEntity<String> deleteLikeFromPost(@Positive @PathVariable("postId") long postId,
                                                     @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromPost(postId, userId);
        return ResponseEntity.ok("Like successfully delete from post");
    }

    @PostMapping("/comment")
    public ResponseEntity<LikeDto> addLikeToComment(@Valid @RequestBody LikeDto likeDto) {
        validator.validAddLikeToComment(likeDto.getCommentId());
        LikeDto createLike = likeService.addLikeToComment(likeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createLike);

    }

    @DeleteMapping("/comment/{commentId}/{userId}")
    public ResponseEntity<String> deleteLikeFromComment(@Positive @PathVariable("commentId") long commentId,
                                                        @Positive @PathVariable("userId") long userId) {
        likeService.deleteLikeFromComment(commentId, userId);
        return ResponseEntity.ok("Like successfully delete from comment");
    }
}