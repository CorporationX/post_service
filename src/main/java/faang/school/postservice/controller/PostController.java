package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;

    @PostMapping("/create")
    public PostDto create(@Valid @RequestBody PostDto postDto) {
        return service.createPost(postDto);
    }

    @PutMapping("/{id}")
    public PostDto publish(@PathVariable long id) {
        return service.publishPost(id);
    }

    @PatchMapping("/{id}")
    public PostDto update(@PathVariable long id, @RequestBody @Validated PostDto postDto) {
        return service.updatePost(id, postDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        service.deletePost(id);
    }

    @GetMapping("/{id}")
    public PostDto get(@PathVariable long id) {
        return service.getPost(id);
    }

    @GetMapping
    public List<PostDto> getFilteredPosts(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Boolean published
    ) {
        return service.getFilteredPosts(authorId, projectId, published);
    }
}
