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

    @PostMapping("/post/{postId}/like")
    public LikeDto likePost(@PathVariable long postId, @RequestBody @Valid LikeDto likeDto) {
        validateId(postId);
        return likeService.likePost(postId, likeDto);
    }

    @GetMapping("/post/{postId}/user/{userId}")
    public void unlikePost(@PathVariable long postId, @PathVariable long userId) {
        validateId(postId);
        validateId(userId);
        likeService.unlikePost(postId, userId);
    }

    private void validateId (long id){
        if (id < 1){
            throw new DataValidationException("Id can't be negative or zero");
        }
    }
}
