package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class AWSPictureService {
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Value("${resources.max-image-pixel-size}")
    private int maxPixelSize;

    public Map<String, MultipartFile> putObjectsToAWS(List<MultipartFile> files, Long postId) {
        return files.stream().
                collect(Collectors.toMap(
                        file -> putObjectToAWS(file, postId),
                        file -> file));
    }

    private String putObjectToAWS(MultipartFile file, Long postId) {
        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        String key = format("%d/%d_%s", postId, System.currentTimeMillis(), (file.getOriginalFilename()));

        try {
            InputStream imageStream = file.getInputStream();
            String format = getFileFormat(file);
            InputStream compressedImageStream = compressImage(imageStream, format);

            s3client.putObject(bucketName, key, compressedImageStream, metadata);
            return key;
        } catch (IOException e) {
            throw new FileException("Failed to convert to InputStream");
        }
    }

    private String getFileFormat(MultipartFile file) {
        String contentType = file.getContentType();
        if (Objects.nonNull(contentType)) {
            return file.getContentType().replace("image/", "");
        } else {
            return "";
        }
    }

    private InputStream compressImage(InputStream imageStream, String format) {
        try {
            BufferedImage image = ImageIO.read(imageStream);
            int width = image.getWidth();
            int height = image.getHeight();

            if (width <= maxPixelSize && height <= maxPixelSize) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                ImageIO.write(image, format, output);

                return new ByteArrayInputStream(output.toByteArray());
            }

            double scaleFactor = width > height ?
                    (double) (maxPixelSize / width) :
                    (double) (maxPixelSize / height);

            Graphics2D graphics = image.createGraphics();
            graphics.scale(scaleFactor, scaleFactor);
            graphics.dispose();

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image, format, output);

            return new ByteArrayInputStream(output.toByteArray());

        } catch (IOException e) {
            throw new FileException("Some file not image");
        }
    }
}
