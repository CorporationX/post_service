package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForCommentDto;
import faang.school.postservice.dto.like.LikeForPostDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/{post_id}/post")
    public ResponseEntity<LikeDto> createLikeForPost(@RequestBody LikeForPostDto likeForPostDto,
                                                     @PathVariable("post_id") long postId) {
        Like like = LikeMapper.likeCreateDtoToLike(likeForPostDto);
        Like saveLike = likeService.likeThePost(like, postId);
        LikeDto likeDto = LikeMapper.likeToResponseLikeDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @PostMapping("/{comment_id}/comment")
    public ResponseEntity<LikeDto> createLikeForComment(@RequestBody LikeForCommentDto likeForCommentDto,
                                                        @PathVariable("comment_id") long commentId) {
        Like like = LikeMapper.likeForCommentToLike(likeForCommentDto);
        Like saveLike = likeService.likeTheComment(like, commentId);
        LikeDto likeDto = LikeMapper.likeToResponseLikeDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @DeleteMapping("/{post_id}/post")
    public ResponseEntity<Void> deleteLikeForPost(@PathVariable("post_id") long postId) {
        likeService.deleteLikeThePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comment_id}/comment")
    public ResponseEntity<Void> deleteLikeForComment(@PathVariable("comment_id") long commentId) {
        likeService.deleteLikeTheComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{post_id}/count_like")
    public ResponseEntity<Long> getCountLike(@PathVariable("post_id") long postId) {
        long countLike = likeService.countTheLikeForPost(postId);
        return ResponseEntity.ok().body(countLike);
    }
}
