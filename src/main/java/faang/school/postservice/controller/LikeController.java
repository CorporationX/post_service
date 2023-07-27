package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @DeleteMapping("/deletelikepost")
    public void deleteLikeOnPost(LikeDto likeDto) {
        validateDto(likeDto.getUserId() == null && likeDto.getComment() == null, "UserId cannot be empty");
        likeService.deleteLikeOnPost(likeDto);
    }

    @DeleteMapping("/deletelikecomment")
    public void deleteLikeOnComment(LikeDto likeDto) {
        validateDto(likeDto.getUserId() == null && likeDto.getComment() == null, "UserId cannot be empty");
        likeService.deleteLikeOnComment(likeDto);
    }

    @GetMapping("/getlikes")
    public List<LikeDto> getAllPostLikes(LikeDto likeDto) {
        validateDto(likeDto.getUserId() == null && likeDto.getPost() == null, "UserId cannot be empty");
        return likeService.getAllPostLikes(likeDto);
    }

    private void validateDto(boolean condition, String message) {
        if (condition) {
            throw new DataValidationException(message);
        }
    }
}
