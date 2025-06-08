package faang.school.postservice.controller.post;

import faang.school.postservice.config.post.media.properties.PostMediaProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostMediaService;
import faang.school.postservice.validation.FileTypeAndSizeValidation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Validated
@RestController
@Tag(name = "Post Media Management", description = "APIs for managing media files associated with posts")
@RequestMapping("/api/v1/posts/{postId}/media")
@RequiredArgsConstructor
public class PostMediaController {
    private final PostMediaService postMediaService;
    private final FileTypeAndSizeValidation fileTypeAndSizeValidation;
    private final PostMediaProperties postMediaProperties;

    @Operation(
            summary = "Add media files to a post",
            description = "Uploads media files to a specific post. Validates file types and sizes based on configuration."
    )
    @PostMapping
    public PostDto addMediaFiles(@PathVariable @NotNull @Positive Long postId,
                             @RequestParam("files") List<MultipartFile> files) {
        log.info("Data validation for media files started for post: {}",
                postMediaProperties.getTypeSpecificSizeLimits());
        fileTypeAndSizeValidation.validate(files, postMediaProperties.getTypeSpecificSizeLimits());
        return postMediaService.addMediaFiles(postId, files);
    }

    @Operation(
            summary = "Delete media files from a post",
            description = "Removes specified media files from a post by their IDs."
    )
    @DeleteMapping
    public PostDto deleteMediaFiles(@PathVariable @NotNull @Positive Long postId,
                                    @RequestParam("fileIds") List<Long> fileIds) {
        log.info("Deleting media files with IDs: {} for post ID: {}", fileIds, postId);
        return postMediaService.deleteMediaFiles(postId, fileIds);
    }

    @Operation(
            summary = "Retrieve media files for a post",
            description = "Fetches all media files associated with a specific post."
    )
    @GetMapping
    public List<InputStream> getMediaFiles(@PathVariable @NotNull @Positive Long postId) {
        log.info("Retrieving media files for post ID: {}", postId);
        return postMediaService.getMediaFiles(postId);
    }
}
