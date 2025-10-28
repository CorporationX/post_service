package faang.school.postservice.controller.post;

import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController implements PostApi{

    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createPostAsDraft(@Valid @RequestBody PostCreateDto postCreateDto) {
        return postService.createPostAsDraft(postCreateDto);
    }

    @PostMapping("/{postId}/publication")
    public PostDto publishPost(@PathVariable Long postId) {
        return postService.publishPost(postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePostSoftly(@PathVariable Long postId) {
        postService.deletePostSoftly(postId);
    }

    @GetMapping
    public PageResponse<PostDto> findAllPublished(
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long projectId,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postService.findAllPublishedByFilter(authorId, projectId, pageable);
    }

    @GetMapping("/{postId}")
    public PostDto findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    @GetMapping("/drafts-by-author")
    public PageResponse<PostDto> findAllDraftsByAuthor(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postService.findAllDraftsByAuthor(pageable);
    }

    @GetMapping("/drafts-by-project")
    public PageResponse<PostDto> findAllDraftsByProject(
            @RequestParam Long projectId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return postService.findAllDraftsByProject(projectId, pageable);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateDto postUpdateDto) {
        return postService.updatePost(postId, postUpdateDto);
    }
}
