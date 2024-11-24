package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/draft/create")
    public ResponseEntity<Void> createDraftPost(@RequestBody @Valid PostDto postDto) {
        postService.createDraftPost(postDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishPost(@PathVariable long id) {
        postService.publishPost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Void> updateContentPost(
            @NotBlank(message = "The content is null")
            @Size(min = 1, max = 4096, message = "The content size is invalid")
            @RequestParam String content,

            @PathVariable long id) {
        postService.updateContentPost(content, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/soft-delete")
    public ResponseEntity<Void> softDeletePost(@PathVariable long id) {
        postService.softDeletePost(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable long id) {
        PostDto post = postService.getPost(id);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/drafts/user/{id}")
    public ResponseEntity<List<PostDto>> getDraftPostsByUserId(@PathVariable long id) {
        List<PostDto> posts = postService.getDraftPostsByUserId(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/drafts/project/{id}")
    public ResponseEntity<List<PostDto>> getDraftPostsByProjectId(@PathVariable long id) {
        List<PostDto> posts = postService.getDraftPostsByProjectId(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/published/user/{id}")
    public ResponseEntity<List<PostDto>> getPublishedPostsByUserId(@PathVariable long id) {
        List<PostDto> posts = postService.getPublishedPostsByUserId(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/published/project/{id}")
    public ResponseEntity<List<PostDto>> getPublishedPostsByProjectId(@PathVariable long id) {
        List<PostDto> posts = postService.getPublishedPostsByProjectId(id);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PatchMapping("/view/{postId}")
    public void viewPost(@PathVariable("postId") long postId) {
        postService.viewPost(postId);
    }
}
