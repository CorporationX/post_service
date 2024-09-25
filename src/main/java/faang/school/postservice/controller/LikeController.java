package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService service;

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersLikedPost(@PathVariable @Positive Long postId,
                                           @RequestHeader(name = "x-user-id") Long header) {
        return service.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikedComm(@PathVariable @Positive Long commentId,
                                           @RequestHeader(name = "x-user-id") Long header) {
        return service.getUsersLikedComm(commentId);
    }
}
