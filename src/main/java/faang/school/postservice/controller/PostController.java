package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Validated
public class PostController {
    private final PostService postService;


    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable @Positive Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping
    public PostDto crateDraftPost(@RequestBody @Validated PostDto postDto) {
        return postService.createDraftPost(postDto);
    }

    @PutMapping("/{id}")
    public PostDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping
    public PostDto updatePost(@RequestBody @Validated PostDto postDto) {
        return postService.updatePost(postDto);
    }

}
