package faang.school.postservice.service.resource.minio;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class MinioImageManager implements MinioManager {
    private final AmazonS3 s3client;

    @Value("${s3.bucketName}")
    private String bucketName;

    @Value("${resources.image.max-pixel-size}")
    private int maxPixelSize;

    @Override
    public ResourceEntity addFileToStorage(MultipartFile file, Post post) {
        String type = file.getContentType();
        String name = file.getOriginalFilename();
        String format = Objects.requireNonNull(type).replace("image/", "");

        try (InputStream compressedFileStream = compressFileStream(file.getInputStream(), format)) {
            int compressedFileSize = compressedFileStream.available();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);
            metadata.setContentLength(compressedFileSize);

            String customKey = format("post_%d/image/%d_%s", post.getId(), System.currentTimeMillis(), name);

            s3client.putObject(bucketName, customKey, compressedFileStream, metadata);

            return ResourceEntity.builder()
                    .key(customKey)
                    .size(compressedFileSize)
                    .name(name)
                    .type(ResourceType.IMAGE)
                    .post(post)
                    .build();

        } catch (IOException e) {
            throw new FileException("Error with image file");
        }
    }

    @Override
    public ResourceEntity updateFileInStorage(String key, MultipartFile newFile, Post post) {
        s3client.deleteObject(bucketName, key);
        return addFileToStorage(newFile, post);
    }

    @Override
    public void removeFileInStorage(String key) {
        s3client.deleteObject(bucketName, key);
    }

    private ByteArrayInputStream compressFileStream(InputStream fileStream, String format) {
        try {
            BufferedImage image = ImageIO.read(fileStream);
            int width = image.getWidth();
            int height = image.getHeight();

            if (!needsResizing(width, height, maxPixelSize)) {
                return getByteArrayInputStream(format, image);
            }

            double scaleFactor = calculateScaleFactor(width, height, maxPixelSize);
            int targetWidth = (int) (width * scaleFactor);
            int targetHeight = (int) (height * scaleFactor);

            BufferedImage compressedImage = compressImage(image, targetWidth, targetHeight);
            return getByteArrayInputStream(format, compressedImage);

        } catch (IOException exception) {
            throw new FileException("Error with image compressing");
        }
    }

    private boolean needsResizing(int width, int height, int maxPixelSize) {
        return width > maxPixelSize || height > maxPixelSize;
    }

    private double calculateScaleFactor(int width, int height, int maxPixelSize) {
        return width > height ?
                (double) maxPixelSize / width :
                (double) maxPixelSize / height;
    }

    private ByteArrayInputStream getByteArrayInputStream(String format, BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, format, output);
        return new ByteArrayInputStream(output.toByteArray());
    }

    private BufferedImage compressImage(BufferedImage image, int targetWidth, int targetHeight) {
        return Scalr.resize(
                image,
                Scalr.Method.AUTOMATIC,
                Scalr.Mode.AUTOMATIC,
                targetWidth,
                targetHeight,
                Scalr.OP_ANTIALIAS);
    }
}
