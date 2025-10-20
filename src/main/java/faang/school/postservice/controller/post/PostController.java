package faang.school.postservice.controller.post;

import faang.school.postservice.controller.facade.post.PostFacade;
import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostFacade postFacade;

    @PostMapping
    public PostDto createDraftPost(@Valid @RequestBody PostCreateDraftDto postCreateDraftDto) {
        return postFacade.createDraftPost(postCreateDraftDto);
    }

    @PatchMapping("/publication/{postId}")
    public PostDto publishedPost(@PathVariable Long postId) {
        return postFacade.publishedPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostDto updatePost(@Valid @PathVariable Long postId, @RequestBody PostUpdateDto postUpdateDto) {
        return postFacade.updatePost(postId, postUpdateDto);
    }

    @DeleteMapping("/{postId}")
    public void updatePost(@PathVariable Long postId) {
        postFacade.deleteById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postFacade.getById(postId);
    }

    @GetMapping("/draft-by-author/{authorId}")
    public List<PostDto> getDraftPostByAuthorId(@PathVariable Long authorId) {
        return postFacade.getDraftPostByAuthorId(authorId);
    }

    @GetMapping("/draft-by-project/{projectId}")
    public List<PostDto> getDraftPostByProjectId(@PathVariable Long projectId) {
        return postFacade.getDraftPostByProjectId(projectId);
    }

    @GetMapping("/published-by-author/{authorId}")
    public List<PostDto> getPublishedPostByAuthorId(@PathVariable Long authorId) {
        return postFacade.getPublishedPostByAuthorId(authorId);
    }

    @GetMapping("/published-by-project/{projectId}")
    public List<PostDto> getPublishedPostByProjectId(@PathVariable Long projectId) {
        return postFacade.getPublishedPostByProjectId(projectId);
    }
}
