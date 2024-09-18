package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.Post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/post/createPost")
    public PostDto create(@RequestBody PostDto postDto) {
        if (!validatePostCreate(postDto)) {
            throw new DataValidationException();
        }
        postDto.setCreatedAt(LocalDateTime.now());

        return postService.create(postDto);
    }

    @PostMapping("/post/publish/{id}")
    public PostDto publish(@PathVariable Long id) {
        return postService.publish(id);
    }

    @PutMapping("/post/update/{id}")
    public PostDto update(@RequestBody PostDto postDto, @PathVariable Long id) {
        postDto.setId(id);
        return postService.update(postDto, id);
    }

    @PutMapping("/post/delete/{id}")
    public PostDto delete(@PathVariable Long id) {
        return postService.delete(id);
    }

    @GetMapping("/post/get/{id}")
    public PostDto get(@PathVariable Long id) {
        return postService.get(id);
    }

    @GetMapping("/post/getAuthorPosts/{id}")
    public List<PostDto> getAllNonPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllNonPublishedByAuthorId(id);
    }

    @GetMapping("/post/getProjectPosts/{id}")
    public List<PostDto> getAllNonPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllNonPublishedByProjectId(id);
    }

    @GetMapping("/post/getPublishedByAuthorId/{id}")
    public List<PostDto> getAllPublishedByAuthorId(@PathVariable Long id) {
        return postService.getAllPublishedByAuthorId(id);
    }

    @GetMapping("/post/getPublishedByProjectId/{id}")
    public List<PostDto> getAllPublishedByProjectId(@PathVariable Long id) {
        return postService.getAllPublishedByProjectId(id);
    }

    private boolean validatePostCreate(PostDto postDto) {
        return ((postDto.getAuthorId() == null && postDto.getProjectId() != null) || (postDto.getAuthorId() != null && postDto.getProjectId() == null)) &&
                (postDto.getContent() != null && !postDto.getContent().isBlank());
    }
}
