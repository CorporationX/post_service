package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.resource.ValidResourceFileSize;
import faang.school.postservice.validator.resource.ValidResourceFileType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/posts")
@Tag(name = "Post Controller", description = "Controller for managing posts")
@ApiResponse(responseCode = "200", description = "Post successfully updated")
@ApiResponse(responseCode = "201", description = "Post successfully created")
@ApiResponse(responseCode = "204", description = "Post successfully deleted")
@ApiResponse(responseCode = "400", description = "Invalid input data")
@ApiResponse(responseCode = "404", description = "Post not found")
@ApiResponse(responseCode = "500", description = "Server Error")
public class PostController {
    private final PostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto create(@RequestBody PostRequestDto postDto) {

        return postService.create(postDto);
    }

    @PutMapping("{id}/publish")
    public PostResponseDto publishPost(@PathVariable Long id) {
        return postService.publishPost(id);
    }


    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PostResponseDto updatePost(
            @PathVariable Long postId,
            @Valid @RequestPart("postDto") PostUpdateDto postDto,

            @Size(max = 10, message = "You can only have 10 images in your post")
            @ValidResourceFileSize(resourceType = "image", maxSizeInBytes = 5 * 1024 * 1024)
            @ValidResourceFileType(resourceType = "image")
            @RequestPart(name = "images", required = false)
            List<MultipartFile> images,

            @Size(max = 10, message = "You can only have 5 audio in your post")
            @ValidResourceFileSize(resourceType = "audio", maxSizeInBytes = 10 * 1024 * 1024)
            @ValidResourceFileType(resourceType = "audio")
            @RequestPart(name = "audio", required = false) List<MultipartFile> audio) {

        log.info("Received request to update post with ID {}. Content: {}, Images: {}, Audio: {}",
                postId,
                postDto.getContent(),
                images != null ? images.size() : 0,
                images != null ? images.size() : 0);

        return postService.updatePost(postId, postDto, images, audio);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId) {
        PostResponseDto responseDto = postService.getPost(postId);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        log.info("Deleted post with ID {}", postId);
    }

    @GetMapping()
    public List<PostResponseDto> getPost(@Valid @ModelAttribute PostFilterDto postFilterDto) {
        return postService.getPosts(postFilterDto);
    }
}