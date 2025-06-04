package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeDto> createLikeForPost(@PathVariable("postId") long postId) {
        Like saveLike = likeService.likeThePost(postId);
        LikeDto likeDto = LikeMapper.likeToResponseLikeDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<LikeDto> createLikeForComment(@PathVariable("commentId") long commentId) {
        Like saveLike = likeService.likeTheComment(commentId);
        LikeDto likeDto = LikeMapper.likeToResponseLikeDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> deleteLikeForPost(@PathVariable("postId") long postId) {
        likeService.deleteLikeThePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteLikeForComment(@PathVariable("commentId") long commentId) {
        likeService.deleteLikeTheComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count-like-post/{postId}")
    public ResponseEntity<List<LikeDto>> getCountLikeForPost(@PathVariable("postId") long postId) {
        List<Like> likes = likeService.getAllTheLikeForPost(postId);
        List<LikeDto> likeDto = LikeMapper.likeListToResponseLikeDto(likes);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }

    @GetMapping("/count-like-comment/{commentId}")
    public ResponseEntity<List<LikeDto>> getCountLikeForComment(@PathVariable("commentId") long commentId) {
        List<Like> likes = likeService.getAllTheLikeForComment(commentId);
        List<LikeDto> likeDto = LikeMapper.likeListToResponseLikeDto(likes);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }
}
