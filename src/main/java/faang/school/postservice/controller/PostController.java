package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/draft")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post createdPost = postService.createDraftPost(post);

        return postMapper.toDto(createdPost);
    }

    @PutMapping("/{id}/publish")
    public PostDto publishPost(@PathVariable Long id) {
        Post publishedPost = postService.publishPost(id);

        return postMapper.toDto(publishedPost);
    }

    @PutMapping("/edit")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post updatedPost = postService.updatePost(post);

        return postMapper.toDto(updatedPost);
    }

    @PutMapping("/delete/{id}")
    public PostDto deletePost(@PathVariable Long id) {
        return postMapper.toDto(postService.deletePost(id));
    }
}