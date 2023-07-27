package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/likepost")
    public LikeDto likePost(LikeDto likeDto) {
        validateDto(likeDto.getUserId() == null, "UserId cannot be empty");
        return likeService.createLikeOnPost(likeDto);
    }

    @PostMapping("/likecomment")
    public LikeDto likeComment(LikeDto likeDto) {
        validateDto(likeDto.getUserId() == null && likeDto.getComment() == null, "UserId cannot be empty");
        return likeService.createLikeOnComment(likeDto);
    }

    private void validateDto(boolean condition, String message) {
        if (condition) {
            throw new RuntimeException(message);
        }
    }
}
