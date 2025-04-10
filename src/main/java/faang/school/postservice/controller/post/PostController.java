package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;

import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/post")
public class PostController {
    private static final String MSG = "cannot be less than 1";
    private final PostService postService;

    @PostMapping
    public PostDto crateDraftPost(@RequestBody @Validated PostDto postDto,
                                  @RequestParam(required = false) MultipartFile[] files) {
        return postService.createDraftPost(postDto, files);
    }

    @PutMapping("/public/{postId}")
    public PostDto publishPost(@PathVariable @Min(value = 1, message = MSG) long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(
            @PathVariable("postId") @Min(value = 1, message = MSG) long postId,
            @RequestBody @Valid PostDto postDto,
            @RequestParam(value = "deletedFileIds", required = false) List<Long> deletedFileIds,
            @RequestParam(value = "addedFiles", required = false) MultipartFile[] addedFiles
    ) {
        return postService.updatePost(postId, postDto, deletedFileIds, addedFiles);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable @Min(value = 1, message = MSG) long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(
            @PathVariable @Min(value = 1, message = MSG) long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/authors/{authorId}/drafts")
    public List<PostDto> getAllAuthorDraftPosts(
            @PathVariable @Min(value = 1, message = MSG) long authorId) {
        return postService.getAllAuthorDraftPosts(authorId);
    }

    @GetMapping("/authors/{authorId}/public")
    public List<PostDto> getAllAuthorPosts(
            @PathVariable @Min(value = 1, message = MSG) long authorId) {
        return postService.getAllAuthorPosts(authorId);
    }

    @GetMapping("projects/{projectId}/drafts")
    public List<PostDto> getAllProjectDraftPosts(
            @PathVariable @Min(value = 1, message = MSG) long projectId) {
        return postService.getAllProjectDraftPosts(projectId);
    }

    @GetMapping("projects/{projectId}/public")
    public List<PostDto> getAllProjectPosts(
            @PathVariable @Min(value = 1, message = MSG) long projectId) {
        return postService.getAllProjectPosts(projectId);
    }
}
