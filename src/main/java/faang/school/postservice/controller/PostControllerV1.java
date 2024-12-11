package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.moderation.sightengine.SightEngineReactiveClient;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@Validated
public class PostControllerV1 {
    private final PostService postService;
    private final SightEngineReactiveClient sightEngineReactiveClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PostDto create(@Validated @RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping("/publish/{id}")
    public PostDto publish(@PathVariable @Positive long id) {
        return postService.publishPost(id);
    }

    @PutMapping("/{id}")
    public PostDto update(@PathVariable long id,
                          @Validated @RequestBody UpdatePostDto updatePostDto) {
        return postService.updatePost(id, updatePostDto);
    }

    @GetMapping("/{id}")
    public PostDto getById(@PathVariable @Positive long id) {
        return postService.getPostDtoById(id);
    }

    @DeleteMapping("/{id}")
    public PostDto delete(@PathVariable @Positive long id) {
        return postService.deletePost(id);
    }

    @GetMapping("/drafts/byUser/{userId}")
    public List<PostDto> getAllDraftNotDeletedPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllDraftNotDeletedPostsByUserId(userId);
    }

    @GetMapping("/drafts/byProject/{projectId}")
    public List<PostDto> getAllDraftNotDeletedPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllDraftNotDeletedPostsByProjectId(projectId);
    }

    @GetMapping("/publications/byUser/{userId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByUserId(@PathVariable @Positive long userId) {
        return postService.getAllPublishedNotDeletedPostsByUserId(userId);
    }

    @GetMapping("/publications/byProject/{projectId}")
    public List<PostDto> getAllPublishedNotDeletedPostsByProjectId(@PathVariable @Positive long projectId) {
        return postService.getAllPublishedNotDeletedPostsByProjectId(projectId);
    }
}
