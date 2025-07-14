package faang.school.postservice.service.resource.upload;

import faang.school.postservice.config.resource.ResourceProperties;
import faang.school.postservice.dto.resource.StoredFile;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.UploadFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAsyncUploadStrategy {

    protected final S3AsyncClient s3AsyncClient;
    protected final ResourceProperties.MediaConfig config;

    @Value("${minio.bucket-name}")
    protected String bucketName;

    public CompletableFuture<List<StoredFile>> upload(List<MultipartFile> files) {
        files.forEach(this::validate);

        List<StoredFile> uploadedFiles = new CopyOnWriteArrayList<>();

        List<CompletableFuture<Void>> futures = files.stream()
                .map(file -> handleUpload(file, uploadedFiles))
                .toList();

        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .handle((ignored, exception) -> {
                    if (exception != null) {
                        log.error("Async upload failed. Starting rollback", exception);
                        rollbackAsync(uploadedFiles);
                        throw new UploadFileException("Upload failed and was rolled back");
                    }
                    return uploadedFiles;
                });
    }

    public boolean supports(String contentType) {
        return config.getAllowedTypes().contains(contentType);
    }

    public void delete(String key) {
        s3AsyncClient.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .whenComplete((res, ex) -> {
                    if (ex != null) {
                        log.error("Failed to delete file from S3: {}", key, ex);
                    } else {
                        log.info("Successfully deleted file from S3: {}", key);
                    }
                });
    }

    private CompletableFuture<Void> handleUpload(MultipartFile file, List<StoredFile> uploadedFiles) {
        try {
            byte[] bytes = processFile(file);
            String key = generateKey(file);
            String contentType = resolveContentType(file);

            return uploadToS3(key, bytes, contentType)
                    .thenRun(() -> uploadedFiles.add(buildStoredFile(file, key, bytes.length)));
        } catch (Exception e) {
            log.error("File processing failed: {}", file.getOriginalFilename(), e);
            throw new UploadFileException("File processing failed before upload: " + file.getOriginalFilename());
        }
    }

    protected void validate(MultipartFile file) {
        if (file.getSize() > config.getMaxFileSizeMb() * 1024 * 1024L) {
            throw new DataValidationException("File size exceeds limit: " + config.getMaxFileSizeMb() + " MB");
        }
        if (!config.getAllowedTypes().contains(file.getContentType())) {
            throw new DataValidationException("Unsupported file type: " + file.getContentType());
        }
    }

    protected CompletableFuture<PutObjectResponse> uploadToS3(String key, byte[] content, String contentType) {
        try {
            return s3AsyncClient.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .contentType(contentType)
                            .contentLength((long) content.length)
                            .build(),
                    AsyncRequestBody.fromBytes(content)
            );
        } catch (Exception e) {
            log.error("S3 upload failed for key {}", key, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    protected void rollbackAsync(List<StoredFile> uploaded) {
        for (StoredFile file : uploaded) {
            s3AsyncClient.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(file.key())
                            .build()
            ).whenComplete((res, ex) -> {
                if (ex != null) {
                    log.error("Rollback failed for file: {}", file.key(), ex);
                } else {
                    log.info("Rolled back file: {}", file.key());
                }
            });
        }
    }

    protected String generateKey(MultipartFile file) {
        return UUID.randomUUID() + "." + getExtension(file);
    }

    protected String getExtension(MultipartFile file) {
        String name = Objects.requireNonNull(file.getOriginalFilename());
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == name.length() - 1) {
            throw new DataValidationException("File must have an extension: " + name);
        }
        return name.substring(dotIndex + 1);
    }

    protected StoredFile buildStoredFile(MultipartFile file, String key, int size) {
        return new StoredFile(
                key,
                file.getOriginalFilename(),
                size,
                getType()
        );
    }

    protected abstract byte[] processFile(MultipartFile file) throws Exception;

    protected abstract String resolveContentType(MultipartFile file);

    protected abstract String getType();
}
