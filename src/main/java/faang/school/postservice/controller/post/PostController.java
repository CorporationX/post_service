package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping("/post")
    public PostDto createDraftPost(@RequestBody PostDto postDto) {
        postValidator.validateAuthorCount(postDto);
        postValidator.validateContentExists(postDto);

        return postService.createDraftPost(postDto);
    }

    public PostDto publishPost(long id) {
        return postService.publishPost(id);
    }

    @PutMapping("/post")
    public PostDto updatePost(@RequestBody PostDto postDto) {
        postValidator.validateIdExists(postDto);
        postValidator.validateContentExists(postDto);
        postValidator.validateAuthorCount(postDto);

        return postService.updatePost(postDto);
    }

    @DeleteMapping("/{id}")
    public boolean deletePost(@PathVariable long id) {
        return postService.deletePost(id);
    }

    @GetMapping("/post/{id}")
    public PostDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/drafts/{userId}")
    public List<PostDto> getDraftsByUser(@PathVariable long userId) {
        return postService.getDraftsByUser(userId);
    }

    @GetMapping("/drafts/{projectId}")
    public List<PostDto> getDraftsByProject(@PathVariable long projectId) {
        return postService.getDraftsByProject(projectId);
    }

    @GetMapping("/posts/{userId}")
    public List<PostDto> getPublishedPostsByUser(@PathVariable long userId) {
    return postService.getPublishedPostsByUser(userId);
    }

    @GetMapping("/posts/{projectId}")
    public List<PostDto> getPublishedPostsByProject(@PathVariable long projectId) {
        return postService.getPublishedPostsByProject(projectId);
    }

}