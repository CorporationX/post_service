package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/posts/{id}")
    public void addToPost(@PathVariable long id) {
        likeService.addToPost(id);
    }

    @PostMapping("/comments/{id}")
    public void addToComment(@PathVariable long id) {
        likeService.addToComment(id);
    }

    @DeleteMapping("/posts/{id}")
    public void deleteFromPost(@PathVariable long id) {
        likeService.deleteFromPost(id);
    }

    @DeleteMapping("/comments/{id}")
    public void deleteFromComment(@PathVariable long id) {
        likeService.deleteFromComment(id);
    }
}
