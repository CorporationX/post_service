package faang.school.postservice.controller;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;
    private final CommentEventPublisher commentEventPublisher;

    @PostMapping
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping("/{id}")
    public PostDto publish(@PathVariable Long id) {
        return postService.publishPost(id);
    }

    @PutMapping("/delete/{id}")
    public PostDto markDeleted(@PathVariable Long id) {
        return postService.deletePost(id);
    }

    @PutMapping
    public PostDto update(@RequestBody PostDto postDto) {
        return postService.updatePost(postDto);
    }

    @PutMapping("/comment")
    public void commentEvent(@RequestBody CommentEvent commentEvent) {
        commentEventPublisher.publish(commentEvent);
    }

    @GetMapping("/{id}")
    public PostDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    @GetMapping("/author_posts/{id}")
    public List<PostDto> getAllNonPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllNonPublishedByAuthorId(id);
    }

    @GetMapping("/project_posts/{id}")
    public List<PostDto> getAllNonPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllNonPublishedByProjectId(id);
    }

    @GetMapping("/published_by_user/{id}")
    public List<PostDto> getAllPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllPublishedByAuthorId(id);
    }

    @GetMapping("/published_by_project/{id}")
    public List<PostDto> getAllPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllPublishedByProjectId(id);
    }
}
