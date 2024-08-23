package faang.school.postservice.controller.like;

import faang.school.postservice.controller.ApiPath;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.LIKES_PATH)
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public void likePost(@Valid LikeDto likeDto) {
        likeService.likePost(likeDto);
    }

    @GetMapping(ApiPath.POST_LIKES_PATH)
    public List<UserDto> getUsersByPostId(@PathVariable Long postId) {
        return likeService.getUsersByPostId(postId);
    }

    @GetMapping(ApiPath.COMMENT_LIKES_PATH)
    public List<UserDto> getUsersByCommentId(@PathVariable Long commentId) {
        return likeService.getUsersByCommentId(commentId);
    }
}
