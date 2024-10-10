package faang.school.postservice.service.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.model.Resource;
import faang.school.postservice.utils.ImageData;
import faang.school.postservice.utils.ImageProcessingUtils;
import faang.school.postservice.utils.ImageRestrictionRule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class S3ServiceImpl implements S3Service {
    @Qualifier("minioS3Client")
    private final AmazonS3 s3Client;

    @Value("${client.s3.minio.bucketName}")
    private String bucketName;

    @Override
    public Resource uploadFile(MultipartFile file, String folder, ImageRestrictionRule rule) throws IOException {
        ImageData imageData;

        boolean isNeedResize = ImageProcessingUtils.isNeedResize(file, rule);
        if (isNeedResize) {
            imageData = retrieveResizedImageData(file, rule);
        } else {
            imageData = retrieveImageData(file);
        }

        String key = generateKey(folder, imageData.getOriginalFilename());

        saveImageToS3(imageData, key);

        return Resource.builder()
                .key(key)
                .size(imageData.getFileSize())
                .createdAt(LocalDateTime.now())
                .type(imageData.getContentType())
                .name(imageData.getOriginalFilename())
                .build();
    }

    @Override
    public void deleteFiles(List<String> keys) {
        String[] keysArray = keys.toArray(new String[0]);
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keysArray);
        s3Client.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public InputStream downloadFile(String key) {
        S3Object s3Object = s3Client.getObject(bucketName, key);
        return s3Object.getObjectContent();
    }

    private ImageData retrieveResizedImageData(MultipartFile file, ImageRestrictionRule rule) throws IOException {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        BufferedImage bufferedImage = ImageProcessingUtils.convertToBufferedImage(file);
        BufferedImage resizedBufferedImage = ImageProcessingUtils.resizeBufferedImage(bufferedImage, rule);

        try (ByteArrayOutputStream bytesFromResizedImage = ImageProcessingUtils.getByteArrayOutputStream(resizedBufferedImage, contentType);
             InputStream input = new ByteArrayInputStream(bytesFromResizedImage.toByteArray())) {

            long fileSize = bytesFromResizedImage.size();
            byte[] content = input.readAllBytes();

            return ImageData.builder()
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .originalFilename(originalFilename)
                    .content(content)
                    .build();
        }
    }

    private ImageData retrieveImageData(MultipartFile file) throws IOException {
        long fileSize = file.getSize();
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        try (InputStream input = file.getInputStream()) {
            byte[] content = input.readAllBytes();
            return ImageData.builder()
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .originalFilename(originalFilename)
                    .content(content)
                    .build();
        }
    }

    private void saveImageToS3(ImageData imageData, String key) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageData.getFileSize());
        objectMetadata.setContentType(imageData.getContentType());

        try (ByteArrayInputStream input = new ByteArrayInputStream(imageData.getContent())) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input, objectMetadata);
            s3Client.putObject(putObjectRequest);
        }
    }

    private String generateKey(String folder, String fileName) {
        return String.format("%s/%d-%s", folder, System.currentTimeMillis(), fileName);
    }
}
