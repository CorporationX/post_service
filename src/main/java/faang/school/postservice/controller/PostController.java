package faang.school.postservice.controller;

import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto create(@Valid @RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publish(@PathVariable Long postId) {
        return postService.publish(postId);
    }

    @PutMapping
    public PostDto update(@Valid @RequestBody PostDto postDto) {
        return postService.update(postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto delete(@PathVariable Long postId) {
        return postService.delete(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/filtered")
    public List<PostDto> getFilteredPosts(@RequestBody PostFilterDto filters) {
        return postService.getFilteredPosts(filters);
    }
}
