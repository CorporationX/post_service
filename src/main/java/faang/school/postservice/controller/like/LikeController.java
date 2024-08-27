package faang.school.postservice.controller.like;

import faang.school.postservice.controller.ApiPath;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.LIKES_PATH)
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public void likePost(@RequestBody @Valid LikeDto likeDto) {
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

  @PostMapping(ApiPath.POST_LIKES_PATH)
  public LikeDto addLikeToPost(@PathVariable long postId, @RequestBody @Valid LikeDto likeDto) {
    return likeService.addLikeToPost(postId, likeDto);
  }

  @PostMapping(ApiPath.COMMENT_LIKES_PATH)
  public LikeDto addLikeToComment(@PathVariable long commentId, @RequestBody @Valid LikeDto likeDto) {
    return likeService.addLikeToComment(commentId, likeDto);
  }

  @DeleteMapping(ApiPath.POST_LIKES_PATH)
  public LikeDto removeLikeFromPost(@PathVariable long postId, @RequestBody @Valid LikeDto likeDto) {
    return likeService.removeLikeFromPost(postId, likeDto);
  }

  @DeleteMapping(ApiPath.COMMENT_LIKES_PATH)
  public LikeDto removeLikeFromComment(@PathVariable long commentId, @RequestBody @Valid LikeDto likeDto) {
    return likeService.removeLikeFromComment(commentId,likeDto);
  }

}
