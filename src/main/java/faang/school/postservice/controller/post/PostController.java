package faang.school.postservice.controller.post;

import faang.school.postservice.controller.facade.post.PostFacade;
import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostFacade postFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createDraftPost(@Valid @RequestBody PostCreateDraftDto postCreateDraftDto) {
        return postFacade.createDraftPost(postCreateDraftDto);
    }

    @PatchMapping("/publication/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto publishedPost(@PathVariable Long postId) {
        return postFacade.publishedPost(postId);
    }

    @PatchMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto updatePost(@Valid @PathVariable Long postId, @RequestBody PostUpdateDto postUpdateDto) {
        return postFacade.updatePost(postId, postUpdateDto);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable Long postId) {
        postFacade.deleteById(postId);
    }

    @GetMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostDto getPostById(@PathVariable Long postId) {
        return postFacade.getById(postId);
    }

    @GetMapping("/draft")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getDraftPostByAuthorId(@RequestParam Long authorId) {
        return postFacade.getDraftPostByAuthorId(authorId);
    }

    @GetMapping("/published")
    @ResponseStatus(HttpStatus.OK)
    public List<PostDto> getPublishedPostByAuthorId(@RequestParam Long authorId) {
        return postFacade.getPublishedPostByAuthorId(authorId);
    }
}
