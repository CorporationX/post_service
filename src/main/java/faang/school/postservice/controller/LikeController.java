package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class LikeController {

    private final LikeService likeService;
    private final LikeRepository likeRepository;


    @GetMapping("/like/{postId}")
    public List<UserDto> getLikeAtPost(@PathVariable Long postId) {
        return likeService.getLikeAtPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getLikeAtComment(@PathVariable Long commentId) {
        List<Long> usersLike = likeRepository.findByCommentId(commentId).stream().map(Like::getUserId).toList();
        if (usersLike.size() <= 100) {
            return likeService.getLikeAtComment(commentId);
        }
        CompletableFuture<List<UserDto>> future = CompletableFuture.supplyAsync(() -> likeService.getLikeAtComment(commentId));
        return future.join();
    }
}
