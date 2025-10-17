package faang.school.postservice.controller.post;

import faang.school.postservice.controller.facade.post.PostFacade;
import faang.school.postservice.dto.post.CreateDraftPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
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
    public PostDto createDraftPost(@RequestBody CreateDraftPostDto createDraftPostDto) {
        return postFacade.createDraftPost(createDraftPostDto);
    }

    @PatchMapping("/publication/{postId}")
    public PostDto publishedPost(@PathVariable Long postId) {
        return postFacade.publishedPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostDto updatePost(@PathVariable Long postId, @RequestBody UpdatePostDto updatePostDto) {
        return postFacade.updatePost(postId, updatePostDto);
    }

    @DeleteMapping("/{postId}")
    public void updatePost(@PathVariable Long postId) {
        postFacade.deleteById(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPostById(@PathVariable Long postId) {
        return postFacade.getById(postId);
    }

    @GetMapping("/draft_by_author/{authorId}")
    public List<PostDto> getDraftPostByAuthorId(@PathVariable Long authorId) {
        return postFacade.getDraftPostByAuthorId(authorId);
    }

    @GetMapping("/draft_by_project/{projectId}")
    public List<PostDto> getDraftPostByProjectId(@PathVariable Long projectId) {
        return postFacade.getDraftPostByProjectId(projectId);
    }

    @GetMapping("/published_by_author/{authorId}")
    public List<PostDto> getPublishedPostByAuthorId(@PathVariable Long authorId) {
        return postFacade.getPublishedPostByAuthorId(authorId);
    }

    @GetMapping("/published_by_project/{projectId}")
    public List<PostDto> getPublishedPostByProjectId(@PathVariable Long projectId) {
        return postFacade.getPublishedPostByProjectId(projectId);
    }
}
