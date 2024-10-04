package faang.school.postservice.controller;


import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;
    private final LikeMapper likeMapper;


    @PostMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponseDto createPostLike(@PathVariable long postId) {
        Like createdLike = likeService.createPostLike(postId);
        return likeMapper.toDto(createdLike);
    }

    @DeleteMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePostLike(@PathVariable long postId) {
        likeService.deletePostLike(postId);
    }

    @PostMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponseDto createCommentLike(@PathVariable long commentId) {
        Like createdLike = likeService.createCommentLike(commentId);
        return likeMapper.toDto(createdLike);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCommentLike(@PathVariable long commentId) {
        likeService.deleteCommentLike(commentId);
    }
}
