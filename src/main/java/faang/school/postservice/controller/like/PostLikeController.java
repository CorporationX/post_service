package faang.school.postservice.controller.like;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.like.PostLikeService;
import faang.school.postservice.validation.like.post.PostLikeAnnotationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("post/like")
@RequiredArgsConstructor
@Validated(PostLikeAnnotationValidator.class)
public class PostLikeController {
    private final PostLikeService postLikeService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LikeDto likePost(@Valid @RequestBody LikeDto dto) {
        return postLikeService.likePost(dto);
    }
    
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deletePostLike(@Valid @RequestBody LikeDto dto) {
        postLikeService.deletePostLike(dto);
    }
}
