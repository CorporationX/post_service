package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likePost")
    public LikeDto likePost(@RequestBody @Valid LikeDto likeDto) {
        return likeService.likePost(likeDto);
    }

    @DeleteMapping("/unlikePost")
    public void unlikePost(@RequestBody @Valid LikeDto likeDto) {
        likeService.unlikePost(likeDto);
    }

    @PostMapping("/likeComment")
    public LikeDto likeComment(@RequestBody @Valid LikeDto likeDto) {
        return likeService.likeComment(likeDto);
    }

    @DeleteMapping("/unlikeComment")
    public void unlikeComment(@RequestBody @Valid LikeDto likeDto) {
        likeService.unlikeComment(likeDto);
    }
}
