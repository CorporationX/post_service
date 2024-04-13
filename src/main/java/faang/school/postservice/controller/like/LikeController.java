package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(description = "test")
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/posts/{postId}/likes")
    @ApiOperation("test get1")
    public List<UserDto> getUsersLikedPost(@PathVariable Long postId){
        return likeService.getUsersLikedPost(postId);
    }

    @GetMapping("/comment/{commentId}/likes")
    @ApiOperation("testGet2")
    public List<UserDto> getUsersLikedComment(@PathVariable Long commentId){
        return likeService.getUsersLikedComment(commentId);
    }
}
