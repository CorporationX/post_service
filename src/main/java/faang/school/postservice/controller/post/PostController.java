package faang.school.postservice.controller.post;

import faang.school.postservice.dto.file.FileMetaData;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post_file.PostFileDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.interfaces.PostService;
import faang.school.postservice.service.post_file.interfaces.PostFileService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/post-service/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;
    private final PostFileService postFileService;

    @PostMapping
    public ResponseEntity<PostDto> createPostDraft(@RequestBody @NotNull PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        PostDto responseBody = postService.createPostDraft(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/publish")
    public ResponseEntity<PostDto> publishPost(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getId());
        PostDto postDtoResponse = postService.publishPost(postDto);

        return ResponseEntity.ok().body(postDtoResponse);
    }

    @PutMapping
    public ResponseEntity<PostDto> updatePost(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getId());
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        return ResponseEntity.ok().body(postService.updatePost(postDto));
    }

    @PostMapping("/delete")
    public ResponseEntity<PostDto> deletePost(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getId());
        return ResponseEntity.ok().body(postService.deletePost(postDto));
    }

    @PostMapping("/get")
    public ResponseEntity<PostDto> getPost(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getId());
        return ResponseEntity.ok().body(postService.getPost(postDto));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable long postId) {
        Post postById = postService.getPostById(postId);
        return ok(postById);
    }

    @PostMapping("/author/drafts")
    public ResponseEntity<List<PostDto>> getAuthorPostDrafts(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getAuthorId());
        return ResponseEntity.ok().body(postService.getAuthorPostDrafts(postDto));
    }

    @PostMapping("/{postId}/files")
    public ResponseEntity<Void> uploadFilesToPost(@RequestHeader("x-user-id") @Min(1) long requesterUserId,
                                                  @PathVariable @Min(1) long postId,
                                                  @RequestPart @NotNull @NotEmpty List<@NotNull MultipartFile> files) {
        postFileService.uploadFilesToPost(postId, files);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/project/drafts")
    public ResponseEntity<List<PostDto>> getProjectPostDrafts(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getProjectId());
        return ResponseEntity.ok().body(postService.getProjectPostDrafts(postDto));
    }

    @GetMapping("/{postId}/files")
    public ResponseEntity<List<PostFileDto>> getPostFilesInfo(@RequestHeader("x-user-id") @Min(1) long requesterUserId,
                                                              @PathVariable @Min(1) long postId) {
        List<PostFileDto> postFilesInfo = postFileService.getPostFilesInfo(postId);
        return ResponseEntity.ok(postFilesInfo);
    }

    @PostMapping("/author/published")
    public ResponseEntity<List<PostDto>> getAuthorPosts(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getAuthorId());
        return ResponseEntity.ok().body(postService.getAuthorPublishedPosts(postDto));
    }

    @DeleteMapping("/{postId}/files/{fileId}")
    public ResponseEntity<Void> deletePostFile(@RequestHeader("x-user-id") @Min(1) long requesterUserId,
                                               @PathVariable @Min(1) long postId,
                                               @PathVariable @Min(1) long fileId) {
        postFileService.deletePostFile(postId, fileId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/project/published")
    public ResponseEntity<List<PostDto>> getProjectPosts(@RequestBody @NotNull PostDto postDto) {
        validateId(postDto.getProjectId());
        return ResponseEntity.ok().body(postService.getProjectPublishedPosts(postDto));
    }

    private void validateId(long id) {
        if (id < 1) {
            throw new PostDtoValidationException("ID must be greater than zero");
        }
    }

    @GetMapping("/{postId}/files/{fileId}")
    public ResponseEntity<byte[]> downLoadPostFile(@RequestHeader("x-user-id") @Min(1) long requesterUserId,
                                                   @PathVariable @Min(1) long postId,
                                                   @PathVariable @Min(1) long fileId) {
        FileMetaData fileMetaData = postFileService.downloadFile(postId, fileId);
        HttpHeaders headers = new HttpHeaders();
        if (fileMetaData.getType() != null) {
            headers.setContentType(MediaType.parseMediaType("%s/%s"
                    .formatted(fileMetaData.getType(), fileMetaData.getExtension())));
        } else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileMetaData.getOriginalName()).build());
        return new ResponseEntity<>(fileMetaData.getData(), headers, HttpStatus.OK);
    }
}
