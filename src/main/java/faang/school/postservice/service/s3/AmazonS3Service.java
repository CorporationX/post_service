package faang.school.postservice.service.s3;

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
    @Value("${services.s3.photoStandards.maxWidthHorizontal}")
    private int maxWidthHorizontal;
    @Value("${services.s3.photoStandards.maxHeightHorizontal}")
    private int maxHeightHorizontal;
    @Value("${services.s3.photoStandards.maxSizeSquare}")
    private int maxSizeSquare;
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        checkFileSize(file);

        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage compressedImage = compressImage(originalImage);

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
        if (files.size() > 10) {
            log.error("Maximum 10 files");
            throw new DataValidationException("You can upload a maximum of 10 files.");
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

    public Resource updateFile(String fileKey, MultipartFile file, String folder) {
        log.info("Update file with key {}, on file {}", fileKey, file.getOriginalFilename());
        deleteFile(fileKey);
        return uploadFile(file, folder);
    }

    public InputStream downloadFile(String fileKey) {
        log.info("Start download file with key {}", fileKey);
        S3Object s3Object = s3Client.getObject(bucketName, fileKey);
        return s3Object.getObjectContent();
    }

    private BufferedImage compressImage(BufferedImage originalImage) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage scaledImage = originalImage;

        if (width > height) {
            if (width > maxWidthHorizontal || height > maxHeightHorizontal) {
                scaledImage = Scalr.resize(originalImage,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.AUTOMATIC,
                        1080, 556);
            }
        } else if (width == height) {
            if (width > maxSizeSquare || height > maxSizeSquare) {
                scaledImage = Scalr.resize(originalImage,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.AUTOMATIC,
                        1080, 1080);
            }
        }

        return scaledImage;
    }

    private void checkFileSize(MultipartFile file) {
        long fileSize = file.getSize();

        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5 MB.");
        }
    }
}
