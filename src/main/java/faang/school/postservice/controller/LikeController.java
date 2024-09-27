package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LikeController {

    @GetMapping("/likePost/{postId}")
    public List<UserDto> getUsersByPostId(@PathVariable long postId) {

    }

    @GetMapping("/likePost/{commentId}")
    public List<UserDto> getUsersByCommentId(@PathVariable long commentId) {

    }

}
