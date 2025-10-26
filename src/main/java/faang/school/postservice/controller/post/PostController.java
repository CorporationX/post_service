package faang.school.postservice.controller.post;

import faang.school.postservice.controller.facade.post.PostFacade;
import faang.school.postservice.dto.post.PostCreateDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import io.swagger.v3.oas.annotations.Parameter;
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
public class PostController implements PostApi {

    private final PostFacade postFacade;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto createDraftPost(@Valid @RequestBody PostCreateDraftDto postCreateDraftDto) {
        return postFacade.createDraftPost(postCreateDraftDto);
    }

    @Override
    @PatchMapping("/publication/{postId}")
    public PostDto publishedPost(@PathVariable Long postId) {
        return postFacade.publishedPost(postId);
    }

    @Override
    @PatchMapping("/{postId}")
    public PostDto updatePost(
            @Parameter(description = "ID поста для обновления", example = "123")
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateDto postUpdateDto) {
        return postFacade.updatePost(postId, postUpdateDto);
    }

    @Override
    @DeleteMapping("/{postId}")
    public void deleteById(
            @Parameter(description = "ID поста для удаления", example = "123")
            @PathVariable Long postId) {
        postFacade.deleteById(postId);
    }

    @Override
    @GetMapping("/{postId}")
    public PostDto getPostById(
            @Parameter(description = "ID поста", example = "123")
            @PathVariable Long postId) {
        return postFacade.getById(postId);
    }

    @Override
    @GetMapping("/draft")
    public List<PostDto> getDraftPostByAuthorId(
            @Parameter(description = "ID автора", example = "456", required = true)
            @RequestParam Long authorId) {
        return postFacade.getDraftPostByAuthorId(authorId);
    }

    @Override
    @GetMapping("/published")
    public List<PostDto> getPublishedPostByAuthorId(
            @Parameter(description = "ID автора", example = "456", required = true)
            @RequestParam Long authorId) {
        return postFacade.getPublishedPostByAuthorId(authorId);
    }
}
