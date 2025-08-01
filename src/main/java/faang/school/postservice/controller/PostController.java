package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final PostControllerFacade postControllerFacade;

    @PostMapping
    public ResponseEntity<ResponsePostDto> createPost(@RequestBody PostCreateDto postCreateDto) {
        ResponsePostDto responsePostDto = postControllerFacade.createPost(postCreateDto);
        return ResponseEntity.ok(responsePostDto);
    }

    @PutMapping("/{postId}/publish")
    public ResponseEntity<ResponsePostDto> publishPost(@PathVariable Long postId) {
        ResponsePostDto responsePostDto = postControllerFacade.publishPost(postId);
        return ResponseEntity.ok(responsePostDto);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponsePostDto> updatePost(@PathVariable Long postId, @RequestBody PostUpdateDto postUpdateDto) {
        ResponsePostDto responsePostDto = postControllerFacade.updatePost(postId, postUpdateDto);
        return ResponseEntity.ok(responsePostDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Long> markPostDeleted(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(postId);
    }

    @GetMapping
    public ResponseEntity<ResponsePostDto> getPostById(@RequestParam Long postId) {
        ResponsePostDto responsePostDto = postControllerFacade.getPostById(postId);
        return ResponseEntity.ok(responsePostDto);
    }

    @GetMapping("/drafts/user")
    public ResponseEntity<List<ResponsePostDto>> getUserDrafts(@RequestParam Long authorId) {
        List<ResponsePostDto> responseList = postControllerFacade.getUserDrafts(authorId);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/drafts/project")
    public ResponseEntity<List<ResponsePostDto>> getProjectDrafts(@RequestParam Long projectId) {
        List<ResponsePostDto> responseList = postControllerFacade.getProjectDrafts(projectId);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ResponsePostDto>> getUserPublished(@RequestParam Long authorId) {
        List<ResponsePostDto> responseList = postControllerFacade.getUserPublished(authorId);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/project")
    public ResponseEntity<List<ResponsePostDto>> getProjectPublished(@RequestParam Long projectId) {
        List<ResponsePostDto> responseList = postControllerFacade.getProjectPublished(projectId);
        return ResponseEntity.ok(responseList);
    }
}