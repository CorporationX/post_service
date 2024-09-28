package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
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
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class S3ImageService implements S3Service {
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${resources.max-image-pixel-size}")
    private int maxPixelSize;

    @Override
    public List<Resource> addFilesToStorage(List<MultipartFile> files, Post post) {
        return files.stream()
                .map(file -> addFileToStorage(file, post))
                .toList();
    }

    @Override
    public Resource updateFileInStorage(String existImageKey, MultipartFile newFile, Post post) {
        s3client.deleteObject(bucketName, existImageKey);
        return addFileToStorage(newFile, post);
    }

    @Override
    public void removeFileInStorage(String key) {
        s3client.deleteObject(bucketName, key);
    }

    private Resource addFileToStorage(MultipartFile file, Post post) {
        String customKey = format("%d/%d_%s", post.getId(), System.currentTimeMillis(), file.getOriginalFilename());
        try {
            String type = file.getContentType();
            String name = file.getOriginalFilename();
            String format = Objects.requireNonNull(type).replace("image/", "");

            InputStream fileStream = file.getInputStream();
            InputStream compressedFileStream = compressFileStream(fileStream, format);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);

            s3client.putObject(bucketName, customKey, compressedFileStream, metadata);

            return Resource.builder()
                    .key(customKey)
                    .size(compressedFileStream.readAllBytes().length)
                    .name(name)
                    .type(ResourceType.IMAGE)
                    .post(post)
                    .build();

        } catch (IOException e) {
            throw new FileException("Error with compressed file");
        }
    }

    private InputStream compressFileStream(InputStream fileStream, String format) {
        try {
            BufferedImage image = ImageIO.read(fileStream);

            int width = image.getWidth();
            int height = image.getHeight();

            if (width <= maxPixelSize && height <= maxPixelSize) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, format, output);
                return new ByteArrayInputStream(output.toByteArray());
            }

            double scaleFactor = width > height ?
                    (double) maxPixelSize / width :
                    (double) maxPixelSize / height;

            int targetWidth = (int) (width * scaleFactor);
            int targetHeight = (int) (height * scaleFactor);

            BufferedImage compressedImage = compressImage(image, targetWidth, targetHeight);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(compressedImage, format, output);
            return new ByteArrayInputStream(output.toByteArray());

        } catch (IOException exception) {
            throw new FileException("Error with compress image");
        }
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
