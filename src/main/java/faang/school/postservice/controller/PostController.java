package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto create(@Valid @RequestBody PostCreateDto postDto) {
        return postService.create(postDto);
    }

    @PutMapping("/publish/{postId}")
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publish(postId);
    }

    @PutMapping("/update")
    public PostDto publishPost(@RequestBody PostUpdateDto postDto) {
        return postService.update(postDto);
    }

    @DeleteMapping("/delete/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getById(@PathVariable Long postId) {
        return postService.getById(postId);
    }

    @PostMapping("/draft")
    public Page<PostDto> getDraftPosts(@RequestBody PostFilterDto postFilterDto) {
        return postService.getPostsByPublishedStatus(postFilterDto);
    }

    @PostMapping("/published")
    public Page<PostDto> getPublishedPosts(@RequestBody PostFilterDto postFilterDto) {
        return postService.getPostsByPublishedStatus(postFilterDto);
    }
}
