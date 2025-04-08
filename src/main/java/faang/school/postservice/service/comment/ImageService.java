package faang.school.postservice.service.comment;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.exception.ImageReadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final AmazonS3 amazonS3;

    @Value("${amazonS3.bucket-name}")
    private  String bucketName;

    @Value("${comment-img.max-size-bytes}")
    private long maxFileSize;

    public ImageKeys uploadResizedImages(MultipartFile file, long id) {
        validateImage(file);

        byte[] originalBytes = null;

        try {
            originalBytes = file.getBytes();
        } catch (IOException e) {
            log.error(ErrorMessages.FAILED_READ.getMessage(), e);
            throw new ImageReadException(ErrorMessages.FAILED_READ.getMessage(), e);
        }

        String largeKey = String.format("comments/%d_large.jpg", id);
        String smallKey = String.format("comments/%d_small.jpg", id);

        Map<String,byte[]> resizedImages = Map.of(
                largeKey, resize(originalBytes, 1080),
                smallKey, resize(originalBytes, 170)
        );

        resizedImages.forEach(this::upload);

        return new ImageKeys(largeKey, smallKey);
    }

    public record ImageKeys(String largeKey, String smallKey) {
    }

    public void deleteImageIfExists(String fileKey) {
        if (fileKey != null && !fileKey.isBlank()) {
            amazonS3.deleteObject(bucketName, fileKey);
        }
    }

    private void upload(String key, byte[] bytes) {
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(bucketName, key, is, metadata);
        } catch (AmazonClientException e) {
            log.error(ErrorMessages.FAILED_UPLOAD.getMessage(), e);
            throw new ImageProcessingException(ErrorMessages.FAILED_UPLOAD.getMessage(), e);

        } catch (IOException e) {
            log.error(ErrorMessages.UNEXPECTED_IO_ERROR.getMessage(), e);
            throw new ImageProcessingException(ErrorMessages.UNEXPECTED_IO_ERROR.getMessage(), e);
        }
    }

    private byte[] resize(byte[] bytes, int maxSize) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .size(maxSize, maxSize)
                    .outputFormat("jpg")
                    .outputQuality(1.0)
                    .toOutputStream(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error(ErrorMessages.FAILED_RESIZE.getMessage(), e);
            throw new ImageProcessingException(ErrorMessages.FAILED_RESIZE.getMessage(),e);
        }
    }


    private void validateImage(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            log.error(ErrorMessages.IMAGE_SIZE_EXCEED.getMessage());
            throw new ImageProcessingException(ErrorMessages.IMAGE_SIZE_EXCEED.getMessage());
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            log.error(ErrorMessages.INVALID_TYPE.getMessage());
            throw new ImageProcessingException(ErrorMessages.INVALID_TYPE.getMessage());
        }

        List<String> allowedTypes = List.of("image/jpeg", "image/png");
        if (!allowedTypes.contains(contentType)) {
            log.error(ErrorMessages.INVALID_FORMAT.getMessage());
            throw new ImageProcessingException(ErrorMessages.INVALID_FORMAT.getMessage());
        }
    }
}
