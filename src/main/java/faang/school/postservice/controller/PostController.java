package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;

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
