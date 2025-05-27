package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    public ResponseEntity<ResponsePostDto> createPost(@RequestBody PostCreateDto postCreateDto) {
        Post post = PostMapper.postUpdateDtoToPost(postCreateDto);
        Post savedPost = postService.createPost(post);
        ResponsePostDto responsePostDto = PostMapper.postToResponsePostDto(savedPost);
        return ResponseEntity.ok(responsePostDto);

    }

    @PutMapping("/{postId}/publish")
    public ResponseEntity<Boolean> publishPost(@PathVariable Long postId) {
        boolean isPublished = postService.publishPost(postId);
        return ResponseEntity.ok(isPublished);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ResponsePostDto> updatePost(@PathVariable Long postId, @RequestBody PostUpdateDto postUpdateDto) {
        Post updatedFields = PostMapper.postUpdateDtoToPost(postUpdateDto);
        Post updatedPost = postService.updatePost(postId, updatedFields);
        ResponsePostDto responsePostDto = PostMapper.postToResponsePostDto(updatedPost);
        return ResponseEntity.ok(responsePostDto);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Long> markPostDeleted(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(postId);
    }

    @GetMapping
    public ResponseEntity<ResponsePostDto> getPostById(@RequestParam Long postId) {
        Post post = postService.getPostById(postId);
        ResponsePostDto responsePostDto = PostMapper.postToResponsePostDto(post);
        return ResponseEntity.ok(responsePostDto);
    }

    @GetMapping("/drafts/user")
    public ResponseEntity<List<ResponsePostDto>> getUserDrafts(@RequestParam Long authorId) {
        List<Post> postList = postService.getAllDraftsByAuthorId(authorId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::postToResponsePostDto).toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/drafts/project")
    public ResponseEntity<List<ResponsePostDto>> getProjectDrafts(@RequestParam Long projectId) {
        List<Post> postList = postService.getAllDraftsByProjectId(projectId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::postToResponsePostDto).toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ResponsePostDto>> getUserPublished(@RequestParam Long authorId) {
        List<Post> postList = postService.getAllPublishedByAuthorId(authorId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::postToResponsePostDto).toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/project")
    public ResponseEntity<List<ResponsePostDto>> getProjectPublished(@RequestParam Long projectId) {
        List<Post> postList = postService.getAllPublishedByProjectId(projectId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::postToResponsePostDto).toList();
        return ResponseEntity.ok(responseList);
    }
}
