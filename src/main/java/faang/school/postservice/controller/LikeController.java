package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @DeleteMapping("/deletelikepost")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnPost(LikeDto likeDto) {
        likeService.deleteLikeOnPost(likeDto);
    }

    @DeleteMapping("/deletelikecomment")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLikeOnComment(LikeDto likeDto) {
        likeService.deleteLikeOnComment(likeDto);
    }

    @GetMapping("/getlikes")
    @ResponseStatus(HttpStatus.OK)
    public List<LikeDto> getAllPostLikes(LikeDto likeDto) {
        return likeService.getAllPostLikes(likeDto);
    }
}
