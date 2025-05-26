package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("posts/{postId}/likes")
    public CreatePostDto addLikeToPost(@PathVariable Long postId) {
        return likeService.addLikeToPost(postId);
    }

    @DeleteMapping("posts/{postId}/likes")
    public CreatePostDto removeLikeFromPost(@PathVariable Long postId) {

        return likeService.removeLikeFromPost(postId);
    }

    @PostMapping("/comments/{commentId}/likes")
    public CommentDto addLikeToComment(@PathVariable Long commentId) {
        return likeService.addLikeToComment(commentId);
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public CommentDto removeLikeFromComment(@PathVariable Long commentId) {
        return likeService.removeLikeFromComment(commentId);
    }
}
