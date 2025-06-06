package faang.school.postservice.controller.post;

import faang.school.postservice.config.post.media.properties.PostMediaProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostMediaService;
import faang.school.postservice.validation.FileTypeAndSizeValidation;
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
@RequestMapping("/api/v1/posts/{postId}/media")
@RequiredArgsConstructor
public class PostMediaController {
    private final PostMediaService postMediaService;
    private final FileTypeAndSizeValidation fileTypeAndSizeValidation;
    private final PostMediaProperties postMediaProperties;

    @PostMapping
    public PostDto addMediaFiles(@PathVariable @NotNull @Positive Long postId,
                             @RequestParam("files") List<MultipartFile> files) {
        log.info("Data validation for media files started for post: {}",
                postMediaProperties.getTypeSpecificSizeLimits());
        fileTypeAndSizeValidation.validate(files, postMediaProperties.getTypeSpecificSizeLimits());
        return postMediaService.addMediaFiles(postId, files);
    }

    @DeleteMapping
    public PostDto deleteMediaFiles(@PathVariable @NotNull @Positive Long postId,
                                    @RequestParam("fileIds") List<Long> fileIds) {
        log.info("Deleting media files with IDs: {} for post ID: {}", fileIds, postId);
        return postMediaService.deleteMediaFiles(postId, fileIds);
    }

    @GetMapping
    public List<InputStream> getMediaFiles(@PathVariable @NotNull @Positive Long postId) {
        log.info("Retrieving media files for post ID: {}", postId);
        return postMediaService.getMediaFiles(postId);
    }
}
