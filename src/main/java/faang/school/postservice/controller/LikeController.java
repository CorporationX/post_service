package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/likepost")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likePost(LikeDto likeDto) {
        return likeService.createLikeOnPost(likeDto);
    }

    @PostMapping("/likecomment")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto likeComment(LikeDto likeDto) {
        return likeService.createLikeOnComment(likeDto);
    }
}
