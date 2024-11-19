package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{id}/likes")
    public List<UserDto> getUsersWhoLikePostByPostId(@Valid @PathVariable @Positive long id) {
        log.info("Request for users by post id: {}", id);
        return likeService.getUsersWhoLikePostByPostId(id);
    }

    @GetMapping("/comment/{id}/likes")
    public List<UserDto> getCommentLikers(@Valid @PathVariable @Positive long id) {
        log.info("Request for users by comment id: {}", id);
        return likeService.getCommentLikers(id);
    }
}
