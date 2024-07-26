package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private static final String COMMENT = "/comment";
    private static final String POST = "/post";
    private final LikeService likeService;

    @PostMapping(POST)
    public LikeDto createLikeToPost(@RequestBody LikeDto likeDto) {
        return likeService.createLikeToPost(likeDto);
    }

    @DeleteMapping(POST)
    public LikeDto removeLikeToPost(@RequestBody LikeDto likeDto) {
        return likeService.removeLikeToPost(likeDto);
    }

    @PostMapping(COMMENT)
    public LikeDto createLikeToComment(@RequestBody LikeDto likeDto) {
        return likeService.createLikeToComment(likeDto);
    }

    @DeleteMapping(COMMENT)
    public LikeDto removeLikeToComment(@RequestBody LikeDto likeDto) {
        return likeService.removeLikeToComment(likeDto);
    }
}
