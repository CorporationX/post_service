package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping
    public PostDto create(PostDto postDto) {
        return postService.create(postDto);
    }

    @PostMapping
    public PostDto publish(PostDto postDto) {
        return postService.publish(postDto.getId());
    }

    @PutMapping
    public PostDto update(PostDto postDto) {
        return postService.update(postDto.getId(), postDto.getContent());
    }

    @DeleteMapping
    public void deleteById(Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping
    public PostDto findById(Long postId) {
        return postMapper.toDto(postService.findById(postId));
    }

    @GetMapping
    public List<PostDto> getAllPostsDraftsByUserAuthorId(Long userId) {
        return postService.getAllPostsDraftsByUserAuthorId(userId);
    }

    @GetMapping
    public List<PostDto> getAllPostsDraftsByProjectAuthorId(Long projectId) {
        return postService.getAllPostsDraftsByProjectAuthorId(projectId);
    }

    @GetMapping
    public List<PostDto> getAllPublishedNonDeletedPostsByUserAuthorId(Long userId) {
        return postService.getAllPublishedNonDeletedPostsByUserAuthorId(userId);
    }

    @GetMapping
    public List<PostDto> getAllPublishedNonDeletedPostsByProjectAuthorId(Long projectId) {
        return postService.getAllPublishedNonDeletedPostsByProjectAuthorId(projectId);
    }
}
