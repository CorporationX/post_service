package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("/hashtags/{hashtag}/firstNew")

    public List<PostDto> getPostsByHashtagOrderByDate(@PathVariable String hashtag) {
        return postService.getPostsByHashtagOrderByDate(hashtag);
    }

    @GetMapping("/hashtags/{hashtag}/firstPopular")
    @Cacheable(value = "hashtags", key = "#hashtag")
    public List<PostDto> getPostsByHashtagOrderByPopularity(@PathVariable String hashtag) {
        return postService.getPostsByHashtagOrderByPopularity(hashtag);
    }
}
