package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/posts/{id}/likes")
    public List<UserDto> getAllUsersByPost(@PathVariable long id) {
        return likeService.getAllUsersByPostId(id);
    }

    @GetMapping("/comments/{id}/likes")
    public List<UserDto> getAllUsersByComment(@PathVariable long id) {
        return likeService.getAllUsersByCommentId(id);
    }
}
