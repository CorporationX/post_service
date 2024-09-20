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
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostMapper postMapper;

    @PostMapping("/create")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post createdPost = postService.createDraftPost(post);

        return postMapper.toDto(createdPost);
    }

    @PutMapping("/publish/{id}")
    public PostDto publishPost(@PathVariable Long id) {
        Post publishedPost = postService.publishPost(id);

        return postMapper.toDto(publishedPost);
    }

    @PutMapping("/update")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        Post post = postMapper.toEntity(postDto);
        Post updatedPost = postService.updatePost(post);

        return postMapper.toDto(updatedPost);
    }

    @DeleteMapping("/delete/{id}")
    public PostDto deletePost(@PathVariable Long id) {
        return postMapper.toDto(postService.deletePost(id));
    }
}