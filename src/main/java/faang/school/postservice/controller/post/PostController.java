package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;


    @GetMapping("/hashtags/{hashtag}")
    public List<PostDto> getPostsByHashtag(@PathVariable String hashtag) {
        if (hashtag == null || hashtag.isBlank()) {
            throw new DataValidationException("Hashtag is empty!");
        }

        return postService.getPostsByHashtag(hashtag);
    }

    //this is temp method
    @PostMapping
    public PostDto publishPost(@RequestBody PostDto postDto) {
        return postService.activate(postDto);
    }

    //this is temp method
    @PutMapping
    public PostDto updatePost(@RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }
}
