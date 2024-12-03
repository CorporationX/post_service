package faang.school.postservice.controller.post;

import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/Posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@RequestBody @Valid PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping("/{id}")
    public PostDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @PatchMapping("/{id}")
    public PostDto updatePost(@PathVariable long id, @Valid @RequestBody PostDto postDto) {
        return postService.updatePost(id, postDto);
    }

    @GetMapping("{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPostDto(id);
    }

    @DeleteMapping("{id}")
    public PostDto deletePost(@PathVariable long id) {
        return postService.deletePost(id);
    }

    @PostMapping("/authors/unposted/{id}")
    public List<PostDto> getAuthorUnpostedPosts(@PathVariable("id") long authorId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(authorId, filterDto);
    }

    @PostMapping("/projects/unposted/{id}")
    public List<PostDto> getProjectUnpostedPosts(@PathVariable("id") long projectId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(projectId, filterDto);
    }

    @PostMapping("/authors/posted/{id}")
    public List<PostDto> getAuthorPostedPosts(@PathVariable("id") long authorId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(authorId, filterDto);
    }

    @PostMapping("/projects/posted/{id}")
    public List<PostDto> getProjectPostedPosts(@PathVariable("id") long projectId, @Valid @RequestBody FilterDto filterDto) {
        return postService.getPostsById(projectId, filterDto);
    }

    @GetMapping("/authors")
    public void checkAndBanAuthors() {
        postService.checkAndBanAuthors();
    }
}
