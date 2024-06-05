package faang.school.postservice.service.s3;

import faang.school.postservice.util.ImageCompressor;
import org.imgscalr.Scalr;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 s3Client;
    private final ImageCompressor imageCompressor;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage compressedImage = imageCompressor.compressImage(originalImage);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, ".jpg", os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

            long fileSize = os.size();
            String fileOriginalName = file.getOriginalFilename();
            String fileContentType = file.getContentType();

            log.info("Start upload file with name {}", file.getOriginalFilename());

            String fileKey = String.format("%s/%d%s", folder, System.currentTimeMillis(), fileOriginalName);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileSize);
            objectMetadata.setContentType(fileContentType);

            s3Client.putObject(bucketName, fileKey, inputStream, objectMetadata);

            Resource resource = Resource.builder()
                    .key(fileKey)
                    .size(fileSize)
                    .createdAt(LocalDateTime.now())
                    .name(fileOriginalName)
                    .type(fileContentType)
                    .build();
            log.info("Created a new resource with key {}", fileKey);

            return resource;

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Resource> uploadFiles(List<MultipartFile> files, String folder) {
        String errorMessage = "You can upload a maximum of 10 files.";
        if (files.size() > 10) {
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }

        List<Resource> resources = files.stream()
                .map(file -> uploadFile(file, folder))
                .toList();
        log.info("Files uploaded successfully.");

        return resources;
    }

    public void deleteFile(String fileKey) {
        s3Client.deleteObject(bucketName, fileKey);
        log.info("Delete file with key {} ", fileKey);
    }

    public InputStream downloadFile(String fileKey) {
        log.info("Start download file with key {}", fileKey);
        S3Object s3Object = s3Client.getObject(bucketName, fileKey);
        return s3Object.getObjectContent();
    }
}
