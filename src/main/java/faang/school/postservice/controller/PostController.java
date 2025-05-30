package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final UserContext userContext;

    @PostMapping()
    public PostResponseDto createPost(@NotNull @RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @PutMapping("/publish/{postId}")
    public PostResponseDto publish(@PathVariable Long postId) {
        return postService.publish(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId) {
        return postService.getPost(postId, userContext.getUserId());
    }

    @PutMapping("/{postId}")
    public PostResponseDto updatePost(@PathVariable Long postId, @NotNull @RequestBody PostDto postDto) {
        return postService.update(postDto, postId);
    }

    @GetMapping
    public List<PostResponseDto> getPostsByIds(@RequestParam List<Long> postIds) {
        return postService.getPostsByIds(postIds);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping("/drafts/author/{authorId}")
    public List<PostResponseDto> getDraftsByAuthor(@PathVariable Long authorId) {
        return postService.findDraftsByAuthorId(authorId, userContext.getUserId());
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostResponseDto> getDraftsByProject(@PathVariable Long projectId) {
        return postService.findDraftsByProjectId(projectId, userContext.getUserId());
    }

    @GetMapping("/published/author/{authorId}")
    public List<PostResponseDto> getPublishedByAuthor(@PathVariable Long authorId) {
        return postService.findPublishedByAuthorId(authorId, userContext.getUserId());
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostResponseDto> getPublishedByProject(@PathVariable Long projectId) {
        return postService.findPublishedByProjectId(projectId, userContext.getUserId());
    }
}
