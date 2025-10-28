package faang.school.postservice.service.post;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.config.s3.S3Properties;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.media.ImageProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMediaService {
    private final AmazonS3 s3;
    private final S3Properties props;
    private final ResourceRepository resourceRepository;

    private String generateKey(Long postId, String originalFilename, String extension) {
        String baseName = Optional.ofNullable(originalFilename)
                .map(FilenameUtils::getBaseName)
                .map(name -> name.replaceAll("[^a-zA-Z0-9_-]", ""))
                .filter(name -> !name.isBlank())
                .orElse("file");

        if (baseName.length() > 12) {
            baseName = baseName.substring(0, 12);
        }

        String timestamp = Long.toString(Instant.now().toEpochMilli(), 36);
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);

        return String.format("p%s_%s_%s_%s.%s", postId, baseName, timestamp, shortUuid, extension);
    }

    public List<Resource> uploadImages(Post post, List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return List.of();

        int existing = post.getResources() == null ? 0 : post.getResources().size();
        if (existing + images.size() > 10) {
            throw new DataValidationException("Too many images: max 10 per post");
        }

        return images.stream().map(file -> {
            try {
                ImageProcessor.Processed processed = ImageProcessor.validateAndResize(file.getBytes());
                String ext = processed.format();
                String key = generateKey(post.getId(), file.getOriginalFilename(), ext);

                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(processed.size());
                meta.setContentType(processed.contentType());

                s3.putObject(props.getBucket(), key,
                        new ByteArrayInputStream(processed.bytes()), meta);

                Resource res = Resource.builder()
                        .key(key)
                        .size(processed.size())
                        .name(file.getOriginalFilename())
                        .type("IMAGE")
                        .post(post)
                        .build();

                return resourceRepository.save(res);

            } catch (IllegalArgumentException iae) {
                log.warn("Failed to validate image: {}", iae.toString());
                throw new DataValidationException(iae.getMessage());
            } catch (Exception e) {
                log.warn("Failed to upload image: {}", file);
                throw new RuntimeException("Failed to upload image", e);
            }
        }).toList();
    }

    public void deleteResources(List<Resource> resources) {
        for (var r : resources) {
            try {
                s3.deleteObject(props.getBucket(), r.getKey());
            } catch (Exception ignore) {
                log.warn("Fail to delete resource from s3: {}", resources);
            }
            resourceRepository.delete(r);
        }
    }
}
