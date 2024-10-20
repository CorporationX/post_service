package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/likes")
public class LikeController {
    private final LikeMapper likeMapper;
    private final LikeService likeService;

    @PostMapping("/posts/like")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToPost(@NonNull @RequestParam Long postId, @RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToPost(postId, fakeLike);

        return likeMapper.toDto(like);
    }

    @PostMapping("/comments/like")
    @ResponseStatus(HttpStatus.CREATED)
    LikeDto addToComment(@NonNull @RequestParam Long commentId, @RequestBody @Validated LikeDto likeDto) {
        Like fakeLike = likeMapper.toEntity(likeDto);
        Like like = likeService.addToComment(commentId, fakeLike);

        return likeMapper.toDto(like);
    }

    @DeleteMapping("/post")
    @ResponseStatus(HttpStatus.OK)
    void removeFromPost(@NonNull @RequestParam Long postId, @RequestParam Long userId) {
        likeService.removeFromPost(postId, userId);
    }

    @DeleteMapping("/comment")
    @ResponseStatus(HttpStatus.OK)
    void removeFromComment(@NonNull @RequestParam Long commentId, @RequestParam Long userId) {
        likeService.removeFromComment(commentId, userId);
    }

    @GetMapping("/posts")
    int getLikesByPost(@NonNull @RequestParam Long postId) {
        return likeService.getLikesByPost(postId);
    }
}
