package faang.school.postservice.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.service.file.FileData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private static final String IMAGE_TYPE = "image";

    private final AmazonS3 amazonS3Client;
    private final ImageCompressionService imageCompressionService;

    @Value("${amazonS3.bucket-name}")
    private String bucketName;

    @Async("asyncModerationExecutor")
    public CompletableFuture<Pair<String, FileData>> uploadFile(FileData fileData, String folder) {
        log.info("Starting uploading file named '{}' to folder named '{}'", fileData.getOriginalName(), folder);
        try {
            byte[] fileBytes = fileData.getType().equals(IMAGE_TYPE)
                    ? compressImage(fileData)
                    : fileData.getData();
            try (ByteArrayInputStream fileStream = new ByteArrayInputStream(fileBytes)) {
                PutObjectRequest request = createPutObjectRequest(fileStream, fileData, folder);
                amazonS3Client.putObject(request);
                log.info("Finished uploading file named '{}' to folder named '{}'", fileData.getOriginalName(), folder);
                return CompletableFuture.completedFuture(Pair.of(request.getKey(), fileData));
            }
        } catch (IOException | SdkClientException e) {
            throw new FileProcessException("Error occurred while uploading file: %s".formatted(fileData.getOriginalName()), e);
        }
    }

    public void deleteFile(String fileKey) {
        log.info("Starting deleting file with key '{}'", fileKey);
        try {
            amazonS3Client.deleteObject(bucketName, fileKey);
            log.info("File with key '{}' was deleted successfully from S3", fileKey);
        } catch (SdkClientException e) {
            throw new FileProcessException("Error occurred while deleting file from S3: %s".formatted(fileKey), e);
        }
    }

    public S3Object getFileFromS3(String fileKey) {
        log.info("Starting downloading file with key '{}'", fileKey);
        try {
            S3Object file = amazonS3Client.getObject(new GetObjectRequest(bucketName, fileKey));
            log.info("File with key '{}' was downloaded successfully from S3", fileKey);
            return file;
        } catch (SdkClientException e) {
            throw new FileProcessException("Error occurred while downloading file from S3: %s".formatted(fileKey), e);
        }
    }

    private PutObjectRequest createPutObjectRequest(InputStream fileStream, FileData fileData, String folder) {
        String fileName = folder + "/" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1, 10001)
                + "_" + fileData.getOriginalName();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("%s/%s".formatted(fileData.getType(), fileData.getExtension()));
        return new PutObjectRequest(bucketName, fileName, fileStream, metadata);
    }

    private byte[] compressImage(FileData fileData) throws IOException {
        byte[] imageData = imageCompressionService.compressImage(fileData.getData(), fileData.getExtension());
        fileData.setData(imageData);
        return imageData;
    }
}
