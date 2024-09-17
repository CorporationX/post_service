package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final PostMapper mapper;

    @PostMapping
    public PostDto createPost(@Valid @RequestBody CreatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post result = postService.create(post);

        return mapper.toDto(result);
    }

    @PutMapping
    public PostDto updatePost(@Valid @RequestBody UpdatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post result = postService.update(post);

        return mapper.toDto(result);
    }

    @PutMapping("/{postId}/publish")
    public PostDto publishPost(@PathVariable Long postId) {
        Post result = postService.publish(postId);

        return mapper.toDto(result);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.delete(postId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<PostDto> searchPosts(FilterPostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        List<Post> posts = postService.search(post);

        return mapper.listEntitiesToListDto(posts);
    }
}
