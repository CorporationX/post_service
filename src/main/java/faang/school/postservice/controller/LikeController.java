package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/like")
    public LikeDto likePost(@RequestBody @Valid LikeDto likeDto) {
        if (likeDto.getPostId() == null) {
            throw new DataValidationException("Post id is required");
        }
        return likeService.likePost(likeDto);
    }

    @GetMapping("/post/{postId}/user/{userId}")
    public void unlikePost(@PathVariable long postId, @PathVariable long userId) {
        likeService.unlikePost(postId, userId);
    }
}
