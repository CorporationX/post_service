package faang.school.postservice.service.amazonS3;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.config.context.s3.AmazonS3Config;
import faang.school.postservice.model.Resource;
import faang.school.postservice.util.ImageCompressor;
import io.lettuce.core.XTrimArgs;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {
    private final AmazonS3 amazonS3Client;
    private final ImageCompressor imageCompressor;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        try {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage compressedImage = imageCompressor.compressImage(originalImage);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, ".jpg", outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            String fileName = file.getOriginalFilename();
            long fileSize = outputStream.size();
            String contentType = file.getContentType();
            String fileKey = String.format("%s%s%d", folder, fileName, System.currentTimeMillis());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(contentType);

            log.info("Start uploading the file {}. AmazonS3Service-uploadFile", fileName);
            amazonS3Client.putObject(bucketName, fileKey, inputStream, metadata);

            return getResource(fileKey, fileName, fileSize, contentType);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static Resource getResource(String fileKey, String fileName, long fileSize, String contentType) {
        return Resource.builder()
                .key(fileKey)
                .name(fileName)
                .size(fileSize)
                .type(contentType)
                .build();
    }
}
