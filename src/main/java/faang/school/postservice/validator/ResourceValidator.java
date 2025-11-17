package faang.school.postservice.validator;

import faang.school.postservice.config.resource.ResourceProperties;
import faang.school.postservice.exception.FileValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceValidator {
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceProperties resourceProperties;

    public void validatePostExists(Long postId) {
        if (!postRepository.existsByIdAndDeletedFalse(postId)) {
            log.warn("Post not found with ID: {}", postId);
            throw new PostNotFoundException("Post with id " + postId + " not found");
        }
    }

    public void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > resourceProperties.getMaxFilesPerPost()) {
            throw new FileValidationException("Maximum " + resourceProperties.getMaxFilesPerPost() + " files allowed per post");
        }

        for (MultipartFile file : files) {
            validateFile(file);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileValidationException("File is empty: " + file.getOriginalFilename());
        }

        if (file.getSize() > resourceProperties.getMaxFileSize()) {
            throw new FileValidationException(
                    String.format("File size exceeds %dMB: %s",
                            resourceProperties.getMaxFileSize() / (1024 * 1024),
                            file.getOriginalFilename())
            );
        }

        if (!isAllowedContentType(file.getContentType())) {
            throw new FileValidationException(
                    String.format("File type not allowed: %s. Allowed types: %s",
                            file.getContentType(), resourceProperties.getAllowedContentTypes())
            );
        }
    }

    private boolean isAllowedContentType(String contentType) {
        return resourceProperties.getAllowedContentTypes()
                .stream()
                .anyMatch(allowed -> Objects.equals(allowed, contentType));
    }


    public void validateFileLimit(Long postId, int newFilesCount) {
        long currentFileCount = resourceRepository.countByPostId(postId);
        if (currentFileCount + newFilesCount > resourceProperties.getMaxFilesPerPost()) {
            throw new IllegalArgumentException(
                    String.format("Cannot add %d files. Post already has %d files. Maximum is %d",
                            newFilesCount, currentFileCount, resourceProperties.getMaxFilesPerPost())
            );
        }
    }

    public void validateFileLimitOnUpdate(Long postId, int newFilesCount, int filesToDeleteCount) {
        long currentCount = resourceRepository.countByPostId(postId);
        long remainingAfterDelete = currentCount - filesToDeleteCount;

        if (remainingAfterDelete + newFilesCount > resourceProperties.getMaxFilesPerPost()) {
            throw new IllegalArgumentException(
                    String.format("Cannot add %d files. After deletion there will be %d files. Maximum is %d",
                            newFilesCount, remainingAfterDelete, resourceProperties.getMaxFilesPerPost())
            );
        }
    }
}
