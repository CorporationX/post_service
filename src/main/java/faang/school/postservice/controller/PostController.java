package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public PostDto create(@RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @PostMapping
    public PostDto publish(@RequestBody PostDto postDto) {
        return postService.publish(postDto.getId());
    }

    @PutMapping
    public PostDto update(@RequestBody PostDto postDto) {
        return postService.update(postDto.getId(), postDto.getContent());
    }

    @DeleteMapping("/{postId}")
    public void deleteById(@PathVariable Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable Long postId) {
        return postMapper.toDto(postService.findById(postId));
    }

    @GetMapping("/draft/user/{userId}")
    public List<PostDto> getAllPostsDraftsByUserAuthorId(@PathVariable Long userId) {
        return postService.getAllPostsDraftsByUserAuthorId(userId);
    }

    @GetMapping("/draft/project/{projectId}")
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(@PathVariable Long projectId) {
        return postService.getAllPostsDraftsByProjectAuthorId(projectId);
    }

    @GetMapping("/published/user/{userId}")
    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(@PathVariable Long userId) {
        return postService.getAllPublishedNonDeletedPostsByUserAuthorId(userId);
    }

    @GetMapping("/published/project/projectId")
    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(@PathVariable Long projectId) {
        return postService.getAllPublishedNonDeletedPostsByProjectAuthorId(projectId);
    }
}
