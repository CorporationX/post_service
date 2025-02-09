package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{postId}/post")
    public ResponseEntity<LikeResponseDto> likePost(@PathVariable long postId,
                                                    @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.likePost(postId, likeRequestDto));
    }

    @DeleteMapping("/{postId}/post/remove")
    public ResponseEntity<LikeResponseDto> removeLikeFromPost(@PathVariable long postId,
                                                              @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.removeLikeFromPost(postId, likeRequestDto));
    }

    @PostMapping("/{commentId}/comment")
    public ResponseEntity<LikeResponseDto> likeComment(@PathVariable long commentId,
                                                       @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.likeComment(commentId, likeRequestDto));
    }

    @DeleteMapping("/{commentId}/comment/remove")
    public ResponseEntity<LikeResponseDto> removeLikeFromComment(@PathVariable long commentId,
                                                                 @RequestBody LikeRequestDto likeRequestDto) {
        return ResponseEntity.ok(likeService.removeLikeFromComment(commentId, likeRequestDto));
    }
}
