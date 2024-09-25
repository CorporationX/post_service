package faang.school.postservice.service.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import faang.school.postservice.model.Resource;
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
        long fileSize;
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        InputStream input;

        boolean isNeedResize = ImageProcessingUtils.isNeedResize(file, rule);
        if (isNeedResize) {
            BufferedImage bufferedImage = ImageProcessingUtils.convertToBufferedImage(file);
            BufferedImage resizedBufferedImage = ImageProcessingUtils.resizeBufferedImage(bufferedImage, rule);
            ByteArrayOutputStream bytesFromResizedImage = ImageProcessingUtils.getByteArrayOutputStream(resizedBufferedImage, contentType);
            fileSize = bytesFromResizedImage.size();
            input = new ByteArrayInputStream(bytesFromResizedImage.toByteArray());
        } else {
            fileSize = file.getSize();
            input = file.getInputStream();
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);

        String key = generateKey(folder, originalFilename);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, input, objectMetadata);
        s3Client.putObject(putObjectRequest);

        return Resource.builder()
                .key(key)
                .size(fileSize)
                .createdAt(LocalDateTime.now())
                .type(contentType)
                .name(originalFilename)
                .build();
    }

    private String generateKey(String folder, String fileName) {
        return String.format("%s/%d-%s", folder, System.currentTimeMillis(), fileName);
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
}
