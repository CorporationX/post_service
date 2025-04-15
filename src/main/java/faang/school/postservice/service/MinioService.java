package faang.school.postservice.service;

import faang.school.postservice.exception.minio_exceptions.BucketNotFoundException;
import faang.school.postservice.exception.minio_exceptions.MinioAccessException;
import faang.school.postservice.exception.minio_exceptions.MinioRemovingFileException;
import faang.school.postservice.exception.minio_exceptions.MinioUploadingFileException;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.model.Resource;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${services.minio.bucket-name}")
    private String bucketName;

    public Resource uploadImage(InputStream inputStream, byte[] imageBytes, String key, String name, String type) {
        checkAndCreateBucket();

        try {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(inputStream, imageBytes.length, -1)
                            .build());

            log.info("File {} uploaded successfully. ETag: {}", key, response.etag());
            return buildResource(key, imageBytes.length, name, type);
        } catch (ErrorResponseException e) {
            String errorMsg = String.format("MinIO error uploading file %s. Code: %s, Message: %s",
                    key, e.errorResponse().code(), e.errorResponse().message());
            log.error(errorMsg);
            throw new MinioUploadingFileException(errorMsg);
        } catch (InsufficientDataException | InternalException e) {
            log.error("IO error while uploading file {}: {}", key, e.getMessage());
            throw new MinioUploadingFileException("Network or IO error during upload");
        } catch (Exception e) {
            log.error("Unexpected error uploading file {}: {}", key, e.getMessage());
            throw new MinioUploadingFileException("Unexpected upload error");
        }
    }

    public Resource uploadVideoOrAudio(MultipartFile file, String key) {
        checkAndCreateBucket();

        try (InputStream inputStream = file.getInputStream()) {
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            log.info("File {} uploaded successfully. Size: {} bytes, ETag: {}",
                    key, file.getSize(), response.etag());

            return Resource.builder()
                    .key(key)
                    .size(file.getSize())
                    .name(file.getOriginalFilename())
                    .type(file.getContentType())
                    .build();

        } catch (ErrorResponseException e) {
            String errorMsg = String.format("MinIO upload failed for %s. Code: %s",
                    key, e.errorResponse().code());
            log.error(errorMsg);
            throw new MinioUploadingFileException(errorMsg);
        } catch (IOException e) {
            log.error("IO error reading file {}: {}", key, e.getMessage());
            throw new MinioUploadingFileException("File read error");
        } catch (Exception e) {
            log.error("Unexpected error uploading {}: {}", key, e.getMessage());
            throw new MinioUploadingFileException("Upload failed");
        }
    }

    public void delete(String key) {
        try {
            if (!exists(key)) {
                throw new BucketNotFoundException.MinioFileNotFoundException(
                        String.format("File %s not found in bucket %s", key, bucketName));
            }

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build());
            log.info("File {} deleted successfully", key);
        } catch (ErrorResponseException e) {
            handleMinioErrorResponse(e, key, "delete");
        } catch (Exception e) {
            log.error("Unexpected error deleting file {}: {}", key, e.getMessage());
            throw new MinioRemovingFileException("Unexpected delete error");
        }
    }

    private void checkAndCreateBucket() {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket {} created successfully", bucketName);
            }
        } catch (ErrorResponseException e) {
            String errorMsg = String.format("MinIO bucket error. Code: %s, Message: %s",
                    e.errorResponse().code(), e.errorResponse().message());
            log.error(errorMsg);
            throw new BucketNotFoundException(errorMsg);
        } catch (Exception e) {
            log.error("Unexpected bucket error: {}", e.getMessage());
            throw new BucketNotFoundException("Unexpected bucket operation error");
        }
    }

    private Resource buildResource(String key, long size, String name, String type) {
        return Resource.builder()
                .key(key)
                .size(size)
                .name(name)
                .type(type)
                .build();
    }

    private boolean exists(String key) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            return true;
        } catch (Exception e) {
            log.debug("File {} not found: {}", key, e.getMessage());
            return false;
        }
    }

    private void handleMinioErrorResponse(ErrorResponseException e, String key, String operation) {
        String errorCode = e.errorResponse().code();
        String errorMessage = e.errorResponse().message();

        String fullMessage = String.format("MinIO %s error for file %s. Code: %s, Message: %s",
                operation, key, errorCode, errorMessage);

        log.error(fullMessage);

        switch (errorCode) {
            case "NoSuchBucket":
                throw new BucketNotFoundException(fullMessage);
            case "NoSuchKey":
                throw new BucketNotFoundException.MinioFileNotFoundException(fullMessage);
            case "AccessDenied":
            case "InvalidAccessKeyId":
            case "SignatureDoesNotMatch":
                throw new MinioAccessException(fullMessage, e);
            default:
                if (operation.equals("delete")) {
                    throw new MinioRemovingFileException(fullMessage);
                } else {
                    throw new MinioUploadingFileException(fullMessage);
                }
        }
    }
}