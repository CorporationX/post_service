package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/users")
public class PostController {

    private final PostService postService;

    @PostMapping("posts/drafts")
    public ResponseEntity<ResponsePostDto> createPost(@RequestBody CreatePostDto createPostDto) {
        Post post = PostMapper.PostDtoToPost(createPostDto);
        Post savedPost = postService.createPost(post);
        ResponsePostDto responsePostDto = PostMapper.PostToResponsePostDto(savedPost);
        return new ResponseEntity<>(
                responsePostDto,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/publications/{postId}")
    public ResponseEntity<Boolean> publishPost(@PathVariable Long postId) {
        boolean isPublished = postService.publishPost(postId);
        return new ResponseEntity<>(
                isPublished,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/{postId}")
    public ResponseEntity<ResponsePostDto> updatePost(@PathVariable Long postId, @RequestBody UpdatePostDto updatePostDto) {
        Post updatedFields = PostMapper.PostDtoToPost(updatePostDto);
        Post updatedPost = postService.updatePost(postId, updatedFields);
        ResponsePostDto responsePostDto = PostMapper.PostToResponsePostDto(updatedPost);
        return new ResponseEntity<>(
                responsePostDto,
                HttpStatus.OK
        );
    }

    @PutMapping("posts/{postId}/deleted")
    public ResponseEntity<Long> markPostDeleted(@PathVariable Long postId) {
        postService.deletePost(postId);
        return new ResponseEntity<>(
                postId,
                HttpStatus.OK
        );
    }

    @GetMapping("posts/{postId}")
    public ResponseEntity<ResponsePostDto> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);
        ResponsePostDto responsePostDto = PostMapper.PostToResponsePostDto(post);
        return new ResponseEntity<>(
                responsePostDto,
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}/posts/drafts")
    public ResponseEntity<List<ResponsePostDto>> getNotDeletedDraftsByUserId(@PathVariable Long userId) {
        List<Post> postList = postService.getNotDeletedDraftsByUserId(userId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::PostToResponsePostDto).toList();
        return new ResponseEntity<>(
                responseList,
                HttpStatus.OK
        );
    }

    @GetMapping("/posts/drafts/{projectId}")
    public ResponseEntity<List<ResponsePostDto>> getNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        List<Post> postList = postService.getNotDeletedDraftsByProjectId(projectId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::PostToResponsePostDto).toList();
        return new ResponseEntity<>(
                responseList,
                HttpStatus.OK
        );
    }

    @GetMapping("/{userId}/posts/publications")
    public ResponseEntity<List<ResponsePostDto>> getNotDeletedPublishedByUserId(@PathVariable Long userId) {
        List<Post> postList = postService.getNotDeletedPublishedByUserId(userId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::PostToResponsePostDto).toList();
        return new ResponseEntity<>(
                responseList,
                HttpStatus.OK
        );
    }

    @GetMapping("/posts/publications/{projectId}")
    public ResponseEntity<List<ResponsePostDto>> getNotDeletedPublishedByProjectId(@PathVariable Long projectId) {
        List<Post> postList = postService.getNotDeletedPublishedByProjectId(projectId);
        List<ResponsePostDto> responseList = postList.stream().map(PostMapper::PostToResponsePostDto).toList();
        return new ResponseEntity<>(
                responseList,
                HttpStatus.OK
        );
    }
}
