package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeForPostDto;
import faang.school.postservice.mapper.like.LikeMapperForPost;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/{post_id}")
    public ResponseEntity<LikeDto> createLike(@RequestBody LikeForPostDto likeForPostDto,
                                              @PathVariable long postId) {
        Like like = likeMapperForPost.toLike(likeForPostDto);
        Like saveLike = likeService.likeThePost(like, postId);
        LikeDto likeDto = likeMapperForPost.toDto(saveLike);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(likeDto);
    }
}
