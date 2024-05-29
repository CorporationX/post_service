package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Validated
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/drafts")
    public PostDto create(@Valid @RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @PostMapping("/published")
    public PostDto publish(@Valid @RequestBody PostDto postDto) {
        return postService.publish(postDto.getId());
    }

    @PutMapping
    public PostDto update(@Valid @RequestBody PostDto postDto) {
        return postService.update(postDto.getId(), postDto.getContent());
    }

    @DeleteMapping("/{postId}")
    public void deleteById(@PathVariable @Min(1) Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable @Min(1) Long postId) {
        return postMapper.toDto(postService.findById(postId));
    }

    @GetMapping("/draft/user/{userId}")
    public List<PostDto> getAllPostsDraftsByUserAuthorId(@PathVariable @Min(1) Long userId) {
        return postService.getAllPostsDraftsByUserAuthorId(userId);
    }

    @GetMapping("/draft/project/{projectId}")
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(@PathVariable @Min(1) Long projectId) {
        return postService.getAllPostsDraftsByProjectAuthorId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(@PathVariable @Min(1) Long userId) {
        return postService.getAllPublishedNonDeletedPostsByUserAuthorId(userId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(@PathVariable @Min(1) Long projectId) {
        return postService.getAllPublishedNonDeletedPostsByProjectAuthorId(projectId);
    }
}