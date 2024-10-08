package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.DeleteImagesFromPostDto;
import faang.school.postservice.dto.post.FilterPostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final PostMapper mapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public PostResponseDto createPost(@Valid @RequestBody CreatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post createdPost = postService.create(post);
        return mapper.toDto(createdPost);
    }

    @PatchMapping
    public PostResponseDto updatePost(@Valid @RequestBody UpdatePostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        Post result = postService.update(post);
        return mapper.toDto(result);
    }

    @PutMapping("/{postId}/publish")
    public PostResponseDto publishPost(@PathVariable Long postId) {
        Post result = postService.publish(postId);
        return mapper.toDto(result);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.delete(postId);
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(@PathVariable Long postId) {
        Post post = postService.get(postId);
        return mapper.toDto(post);
    }

    @GetMapping("/search/{authorId}/author")
    public List<PostResponseDto> searchPostsByAuthor(@PathVariable Long authorId, FilterPostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        post.setAuthorId(authorId);
        List<Post> posts = postService.searchByAuthor(post);
        return mapper.toDtos(posts);
    }

    @GetMapping("/search/{projectId}/project")
    public List<PostResponseDto> searchPostsByProject(@PathVariable Long projectId, FilterPostRequestDto requestDto) {
        Post post = mapper.toEntity(requestDto);
        post.setProjectId(projectId);
        List<Post> posts = postService.searchByProject(post);
        return mapper.toDtos(posts);
    }

    @PostMapping(value = "/{postId}/upload-images")
    @ResponseStatus(NO_CONTENT)
    public void uploadImages(@PathVariable Long postId, List<MultipartFile> images) {
        postService.uploadImages(postId, images);
    }

    @GetMapping("/image/{resourceId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadImage(@PathVariable Long resourceId) {
        Resource foundResource = postService.findResourceById(resourceId);
        org.springframework.core.io.Resource imageResource = postService.downloadImage(foundResource);
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(foundResource.getType()))
                .body(imageResource);
    }

    @DeleteMapping("/images")
    @ResponseStatus(NO_CONTENT)
    public void deleteImages(@Valid @RequestBody DeleteImagesFromPostDto deleteImagesFromPostDto) {
        postService.deleteImagesFromPost(deleteImagesFromPostDto.getResourceIds());
    }
}
