package faang.school.postservice.service.amazonS3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.util.ImageCompressor;
import faang.school.postservice.validator.image.ImageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {
    private final AmazonS3 amazonS3Client;
    private final ImageCompressor imageCompressor;
    private final ImageValidator imageValidator;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public ResourceDto uploadFile(MultipartFile file, String folder) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage compressedImage = imageCompressor.compressImage(originalImage);

            String fileName = file.getOriginalFilename();
            String formatName = imageCompressor.getImageFormat(fileName);
            imageValidator.validateImageFormat(formatName);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, formatName, outputStream);

            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


            long fileSize = outputStream.size();
            String contentType = file.getContentType();
            String fileKey = String.format("%d%s%s", System.currentTimeMillis(), folder, fileName);
            ObjectMetadata metadata = getObjectMetadata(fileSize, contentType);

            log.info("Start uploading the file {}. AmazonS3Service-uploadFile", fileName);
            amazonS3Client.putObject(bucketName, fileKey, inputStream, metadata);

            return getResource(fileKey, fileName, fileSize, contentType);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static ObjectMetadata getObjectMetadata(long fileSize, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(contentType);
        return metadata;
    }

    private ResourceDto getResource(String fileKey, String fileName, long fileSize, String contentType) {
        return ResourceDto.builder()
                .key(fileKey)
                .name(fileName)
                .size(fileSize)
                .type(contentType)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void deleteFile(String fileKey) {
        log.info("Deleting file with key = {}", fileKey);
        amazonS3Client.deleteObject(bucketName, fileKey);
    }

    public List<ResourceDto> uploadFiles(List<MultipartFile> files, String folderName) {
        log.info("Start uploading List of files");
        return files.stream().map(file -> uploadFile(file, folderName)).toList();
    }

    public InputStream downloadFile(String fileKey) {
        log.info("Start downloading File {}", fileKey);
        S3Object s3Object = amazonS3Client.getObject(bucketName, fileKey);

        return s3Object.getObjectContent();
    }
}
