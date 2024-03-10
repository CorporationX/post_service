package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Validated
public class PostController {

    private final PostService postService;
    private final UserContext userContext;

    @PostMapping("/draft")
    public PostDto createPostDraft(@RequestPart @Valid PostDto postDto,
                                @RequestPart(value = "files", required = false)
                                @Size(max = 10) List<MultipartFile> files) {

        return postService.createPostDraft(postDto, files);
    }

    @PostMapping("/publish/{postId}")
    public void publishPost(@PathVariable long postId) {
        postService.publishPost(postId, userContext.getUserId());
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @RequestPart @Valid PostDto postDto,
                           @RequestPart(value = "files", required = false)
                           @Size(max = 10) List<MultipartFile> files) {
        return postService.updatePost(postId, userContext.getUserId(), postDto, files);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable long postId) {
        postService.deletePost(postId, userContext.getUserId());
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @GetMapping("/author/drafts/{authorId}")
    public List<PostDto> getAuthorDrafts(@PathVariable long authorId) {
        return postService.getAuthorDrafts(authorId);
    }

    @GetMapping("/project/drafts/{projectId}")
    public List<PostDto> getProjectDrafts(@PathVariable long projectId) {
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/author/posts/{authorId}")
    public List<PostDto> getAuthorPosts(@PathVariable long authorId) {
        return postService.getAuthorPosts(authorId);
    }

    @GetMapping("/project/posts/{projectId}")
    public List<PostDto> getProjectPosts(@PathVariable long projectId) {
        return postService.getProjectPosts(projectId);
    }
}
