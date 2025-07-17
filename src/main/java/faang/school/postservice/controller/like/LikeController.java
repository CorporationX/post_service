package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{id}/likes")
    public void addToPost(@PathVariable long id) {
        likeService.addToPost(id);
    }

    @PostMapping("/comments/{id}/likes")
    public void addToComment(@PathVariable long id) {
        likeService.addToComment(id);
    }

    @DeleteMapping("/posts/{id}/likes")
    public void deleteFromPost(@PathVariable long id) {
        likeService.deleteFromPost(id);
    }

    @DeleteMapping("/comments/{id}/likes")
    public void deleteFromComment(@PathVariable long id) {
        likeService.deleteFromComment(id);
    }
}
