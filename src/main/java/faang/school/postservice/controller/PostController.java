package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService service;
    private final PostViewEventPublisher publisher;

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
    public PostDto get(@PathVariable long id, @RequestParam Long userId) {
        PostDto postDto = service.getPost(id);
        Long authorId = postDto.getAuthorId();
        Long postId = postDto.getId();
        PostViewEvent event = new PostViewEvent(postId, authorId, userId, LocalDateTime.now());
        publisher.publish(event);
        return postDto;
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
