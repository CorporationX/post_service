package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validate.post.PostValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final PostValidator postValidator;

    @PostMapping
    public PostDto create(@RequestBody @Valid PostDto postDto) {
        postValidator.validateCreate(postDto);
        return postService.create(postDto);
    }

    @PostMapping("/{postId}")
    public PostDto publish(@PathVariable Long postId) {
        return postService.publish(postId);
    }

    @PutMapping("/{postId}")
    public PostDto update(@PathVariable Long postId, @RequestBody @Valid PostDto postDto) {
        return postService.update(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto softDelete(@PathVariable Long postId) {
        return postService.softDelete(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getById(@PathVariable Long postId) {
        return postService.getById(postId);
    }

    @GetMapping("/published/author/{authorId}")
    public List<PostDto> getAllPublishedPostsForAuthor(@PathVariable Long authorId) {
        return postService.getAllPublishedPostsForAuthor(authorId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getAllPublishedPostsForProject(@PathVariable Long projectId) {
        return postService.getAllPublishedPostsForProject(projectId);
    }

    @GetMapping("/unpublished/author/{authorId}")
    public List<PostDto> getAllUnPublishedPostsForAuthor(@PathVariable Long authorId) {
        return postService.getAllUnpublishedPostsForAuthor(authorId);
    }

    @GetMapping("/unpublished/project/{projectId}")
    public List<PostDto> getAllUnpublishedPostsForProject(@PathVariable Long projectId) {
        return postService.getAllUnpublishedPostsForProject(projectId);
    }
}
