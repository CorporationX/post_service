package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/likesPost/{postId}")
    public List<UserDto> getUsersWhoLikesByPostId(@Min(0) @PathVariable Long postId) {
        return likeService.getUsersWhoLikesByPostId(postId);
    }

    @GetMapping("/likeComment/{commentId}")
    public List<UserDto> getUsersWhoLikesByCommentId(@Min(0) @PathVariable Long commentId) {
        return likeService.getUsersWhoLikesByCommentId(commentId);
    }
}
