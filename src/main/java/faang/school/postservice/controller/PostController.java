package faang.school.postservice.controller;

import faang.school.postservice.dto.resource.ResourceDtoRs;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.exception.DownloadFileException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final ResourceService resourceService;

    @PostMapping
    public PostResponseDto createPostDraft(@RequestBody PostCreateRequestDto postCreateRequestDto) {
        return postService.createPostDraft(postCreateRequestDto);
    }

    @PatchMapping("/{id}/publish")
    public PostResponseDto publishPostDraft(@PathVariable("id") Long postId) {
        return postService.publishPostDraft(postId);
    }

    @PutMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable("id") Long postId,
                                      @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        return postService.updatePost(postId, postUpdateRequestDto);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable("id") Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/")
    public List<PostResponseDto> getFilteredPosts(@RequestParam Boolean isPublished,
                                                  @RequestParam(required = false) Long projectId,
                                                  @RequestParam(required = false) Long authorId) {

        PostFilterDto postFilter = PostFilterDto.builder()
                .authorId(authorId)
                .projectId(projectId)
                .isPublished(isPublished)
                .build();

        return postService.findAllByFilter(postFilter);
    }

    @PutMapping(value = "/{postId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ResourceDtoRs> uploadFile(
            @PathVariable long postId,
            @RequestPart("files") MultipartFile[] files) {
        log.info("Received a request to save a files for a post with id = {}", postId);
        return postService.uploadFiles(postId, files);
    }

    @GetMapping(value = "/resources/{resourceId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId){
        log.info("Received a request to download a file with resourceId = {}", resourceId);
        byte[] imageBytes = null;
        try {
            imageBytes = resourceService.downloadResource(resourceId).readAllBytes();
        }catch (IOException e) {
            log.error("Error with downloading file with id {}", resourceId);
            throw new DownloadFileException("Error with downloading file with id %d".formatted(resourceId), e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }
}
