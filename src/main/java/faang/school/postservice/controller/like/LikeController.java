package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForCommentDto;
import faang.school.postservice.dto.like.LikeForPostDto;
import faang.school.postservice.mapper.like.LikeMapperForComment;
import faang.school.postservice.mapper.like.LikeMapperForPost;
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
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    private final LikeMapperForPost likeMapperForPost;
    private final LikeMapperForComment likeMapperForComment;

    @PostMapping("/like_post/{post_id}")
    public ResponseEntity<LikeDto> createLikeForPost(@RequestBody LikeForPostDto likeForPostDto,
                                              @PathVariable("post_id") long postId) {
        Like like = likeMapperForPost.toLike(likeForPostDto);
        Like saveLike = likeService.likeThePost(like, postId);
        LikeDto likeDto = likeMapperForPost.toDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @PostMapping("/like_comment/{comment_id}")
    public ResponseEntity<LikeDto> createLikeForComment(@RequestBody LikeForCommentDto likeForCommentDto,
                                                        @PathVariable("comment_id") long commentId) {
        Like like = likeMapperForComment.toLike(likeForCommentDto);
        Like saveLike = likeService.likeTheComment(like, commentId);
        LikeDto likeDto = likeMapperForComment.toDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @DeleteMapping("/like_post/{post_id}")
    public ResponseEntity<Void> deleteLikeForPost(@PathVariable("post_id") long postId) {
        likeService.deleteLikeThePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/like_comment/{comment_id}")
    public ResponseEntity<Void> deleteLikeForComment(@PathVariable("comment_id") long commentId) {
        likeService.deleteLikeTheComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count_like/{post_id}")
    public ResponseEntity<Long> getCountLike(@PathVariable("post_id") long postId) {
        long countLike = likeService.countTheLikeForPost(postId);
        return ResponseEntity.ok().body(countLike);
    }
}
