package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MediaFileTypeValidator implements FileTypeAndSizeValidation {
    private final Tika tika = new Tika();

    @Override
    public void validate(List<MultipartFile> files,
                         Map<String,Long> typeSpecificSizeLimits) {

        if (files == null || files.isEmpty()) {
            log.warn("List of files is not provided or empty.");
            throw new DataValidationException("List of files is not provided or empty.");
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                log.warn("One of the files in the list is not provided or is empty.");
                throw new DataValidationException("One of the files in the list is not provided or is empty.");
            }

            String originalFilename = file.getOriginalFilename();
            String detectedMimeType = detectMimeTypeInternal(file);

            validateFileType(detectedMimeType, originalFilename, typeSpecificSizeLimits);
            validateFileSize(detectedMimeType, originalFilename, typeSpecificSizeLimits, file);

            log.info("File '{}' (type: {}, size: {} bytes) successfully passed validation.",
                    originalFilename, detectedMimeType, file.getSize());
        }

    }

    private String detectMimeTypeInternal(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            String detectedType = tika.detect(inputStream);
            if (detectedType == null
                    || detectedType.isEmpty()
                    || "application/octet-stream".equalsIgnoreCase(detectedType)) {
                log.warn("Tika could not determine a specific media type for file: {}. Detected as: {}",
                        originalFilename, detectedType);
                throw new DataValidationException(
                        "Could not determine a specific media type from content for file: " + originalFilename);
            }
            return detectedType;
        } catch (IOException e) {
            log.error("Error reading file {} to determine type: {}", originalFilename, e.getMessage(), e);
            throw new RuntimeException("Error reading file to determine type: " + originalFilename, e);
        }
    }

    private void validateFileType(String detectedMimeType,
                                  String originalFilename,
                                  Map<String, Long> typeSpecificSizeLimits) {

        if (typeSpecificSizeLimits.keySet().stream().noneMatch(detectedMimeType::startsWith)) {
            log.warn("File type ('{}') for file {} is not allowed. Allowed prefixes: {}",
                    detectedMimeType, originalFilename, typeSpecificSizeLimits.keySet());
            throw new DataValidationException(
                    "File type ('" + detectedMimeType + "') is not an allowed type."
            );
        }
    }

    private void validateFileSize(String detectedMimeType,
                                  String originalFilename,
                                  Map<String, Long> typeSpecificSizeLimits,
                                  MultipartFile file) {

        String baseMimeType = detectedMimeType.split("/")[0];
        long maxSizeInBytes = typeSpecificSizeLimits.getOrDefault(baseMimeType, -1L);

        if (maxSizeInBytes == -1L) {
            log.warn("No size limit defined for type '{}' of file {}.", baseMimeType, originalFilename);
            throw new DataValidationException("No size limit defined for file type: '" + baseMimeType + "'.");
        }

        if (file.getSize() > maxSizeInBytes) {
            log.warn("File {} with size {} bytes (type: {}) exceeds the limit of {} bytes for type '{}'.",
                    originalFilename, file.getSize(), detectedMimeType, maxSizeInBytes, baseMimeType);
            throw new DataValidationException(
                    String.format("File size for '%s' (%.2f MB, type: %s) exceeds the limit of %.2f MB for type '%s'.",
                            originalFilename, (double) file.getSize() / (1024 * 1024), detectedMimeType,
                            (double) maxSizeInBytes / (1024 * 1024), baseMimeType)
            );
        }
    }
}
