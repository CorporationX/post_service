package faang.school.postservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/posts")
@Validated
public class PostController {
    private final PostServiceImpl postService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody CreatePostDto dto) {
        postService.create(dto);
    }

    @PutMapping("/{postId}")
    public PostDto update(@PathVariable long postId, @RequestBody UpdatePostDto dto) {
        return postService.update(postId, dto);
    }

    @PatchMapping("/{postId}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void publish(@PathVariable long postId) {
        postService.publish(postId);
    }

    @PatchMapping("/{postId}/soft-delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDelete(@PathVariable long postId) {
        postService.softDelete(postId);
    }

    @PatchMapping("/{postId}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable long postId) {
        postService.restore(postId);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long postId) {
        postService.delete(postId);
    }

    @GetMapping("/author/{authorId}")
    public List<PostDto> getProjectsByAuthorId(
            @PathVariable long authorId,
            @RequestParam(defaultValue = "false") boolean deleted,
            @RequestParam(defaultValue = "true") boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return postService.getPostsByAuthorId(authorId, deleted, published, page, size, sortBy, sortDirection);
    }

    @GetMapping("/project/{projectId}")
    public List<PostDto> getProjectsByProjectId(
            @PathVariable long projectId,
            @RequestParam(defaultValue = "false") boolean deleted,
            @RequestParam(defaultValue = "true") boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        return postService.getPostsByProjectId(projectId, deleted, published, page, size, sortBy, sortDirection);
    }

    @GetMapping("{postId}")
    public Post getPost(@PathVariable long postId) {
        return postService.getPostByIdOrThrow(postId);
    }
}
