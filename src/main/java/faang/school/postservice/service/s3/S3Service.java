package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 clientAmazonS3;
    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource uploadFile(MultipartFile file, String folder) {
        long fileSize = file.getSize();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileSize);
        metadata.setContentType(file.getContentType());
        String key = folder + "/" + file.getOriginalFilename();
        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);
            processAndPutImage(request);
        } catch (IOException e) {
            log.error("Failed to upload file: ", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
        return Resource.builder()
                .key(key)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .size(fileSize)
                .build();
    }

    public void deleteFile(String key) {
        clientAmazonS3.deleteObject(bucketName, key);
    }

    public InputStream downloadFile(String key) {
        try {
            return clientAmazonS3.getObject(bucketName, key).getObjectContent();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void processAndPutImage(PutObjectRequest request) throws IOException {
        BufferedImage image = ImageIO.read(request.getInputStream());
        int width = image.getWidth();
        int height = image.getHeight();
        int maxValue = 1080;
        int anotherMaxValue = 566;
        if ((width > height) && (width > maxValue) && (height > anotherMaxValue)) {
            scaleAndPutImage(image, maxValue, anotherMaxValue, request);
        } else if ((width == height) && (width > maxValue) && (height > maxValue)) {
            scaleAndPutImage(image, maxValue, maxValue, request);
        } else if ((width < height) && (width > anotherMaxValue) && (height > maxValue)) {
            scaleAndPutImage(image, anotherMaxValue, maxValue, request);
        } else {
            clientAmazonS3.putObject(request);
        }
    }

    private void scaleAndPutImage(BufferedImage image, int maxWidth, int maxHeight, PutObjectRequest request) throws IOException {
        BufferedImage resizedImage = createResizedCopy(image, maxWidth, maxHeight);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        request.setInputStream(inputStream);
        request.getMetadata().setContentLength(inputStream.available());
        request.getMetadata().setContentType("image/png");
        os.close();
        inputStream.close();
        clientAmazonS3.putObject(request);
    }

    private BufferedImage createResizedCopy(BufferedImage originalImage, int scaledWidth, int scaledHeight) {
        // создаем новое изображение нужного разрешения
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        // получаем объект, предназначенный для "рисования" на новом изображении
        Graphics2D g = scaledBI.createGraphics();
        // рисуем на новом изображении наше оригинальное с нужным нам разрешением
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose(); // освобождаем ресурсы, что бы не было утечки памяти

        return scaledBI;
    }
}
