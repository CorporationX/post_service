package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/drafts/user/{id}")
    public List<PostDto> getDraftsByUser(@PathVariable Long id) {
        return postService.getDraftsByUser(id);
    }

    @GetMapping("/drafts/project/{id}")
    public List<PostDto> getDraftsByProject(@PathVariable Long id) {
        return postService.getDraftsByProject(id);
    }

    @GetMapping("/published/user/{id}")
    public List<PostDto> getPublishedByUser(@PathVariable Long id) {
        return postService.getPublishedByUser(id);
    }

    @GetMapping("/published/project/{id}")
    public List<PostDto> getPublishedByProject(@PathVariable Long id) {
        return postService.getPublishedByProject(id);
    }

    @GetMapping("/{id}")
    public PostDto getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    @PostMapping("/create")
    public PostDto createDraft(@RequestBody PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            return postService.createDraft(postDto);
        } else {
            throw new DataValidationException("Invalid post! ProjectId or authorId is null!");
        }

    }

    @PostMapping("/publish/{postId}")
    public PostDto publishDraft(@PathVariable Long postId) {
        return postService.publishDraft(postId);
    }

    @PostMapping("/update/{id}")
    public PostDto updatePost(@PathVariable Long id, @RequestBody PostUpdateDto postUpdateDto) {
        return postService.updatePost(id, postUpdateDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.status(200).body(Map.of("message", "Deleted post with id: " + id));
    }
}
