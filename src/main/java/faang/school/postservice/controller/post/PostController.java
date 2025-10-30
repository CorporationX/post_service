package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("/drafts")
    public PostDto createDraft(@Valid @RequestBody PostDto postDto) {
        validateIds(postDto);
        if (postDto.published() || postDto.deleted()) {
            log.error("Draft is published/deleted before creation");
            throw new DataValidationException("Draft cannot be published or deleted");
        }
        return postService.createDraft(postDto);
    }

    @PostMapping("/drafts/{postId}")
    public PostDto publishPost(@PathVariable long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@PathVariable long postId, @Valid @RequestBody PostDto postDto) {
        validateIds(postDto);
        if (!postDto.published() || postDto.deleted()) {
            log.error("Post #{} is neither published nor deleted", postId);
            throw new DataValidationException("Unpublished or deleted posts cannot be updated");
        }
        return postService.updatePost(postId, postDto);
    }

    @DeleteMapping("/{postId}")
    public PostDto deletePost(@PathVariable long postId) {
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDto findPostById(@PathVariable long postId) {
        return postService.findPostById(postId);
    }

    @GetMapping("/drafts/authors/{authorId}")
    public List<PostDto> findDraftsByAuthorId(@PathVariable long authorId) {
        return postService.findDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/projects/{projectId}")
    public List<PostDto> findDraftsByProjectId(@PathVariable long projectId) {
        return postService.findDraftsByProjectId(projectId);
    }

    @GetMapping("/authors/{authorId}")
    public List<PostDto> findPostsByAuthorId(@PathVariable long authorId) {
        return postService.findPostsByAuthorId(authorId);
    }

    @GetMapping("/projects/{projectId}")
    public List<PostDto> findPostsByProjectId(@PathVariable long projectId) {
        return postService.findPostsByProjectId(projectId);
    }

    private void validateIds(PostDto postDto) {
        if (postDto.authorId() == null && postDto.projectId() == null) {
            log.error("Nobody is trying to act the Post");
            throw new DataValidationException("Author or Project must exist");
        }
        if (postDto.authorId() != null && postDto.projectId() != null) {
            log.error("Post is being acted by Author #{} and Project #{} at the same time",
                    postDto.authorId(), postDto.projectId());
            throw new DataValidationException("Unable to act Post by Author and Project at the same time");
        }
    }
}
