package faang.school.postservice.service.amazonS3;

import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.validation.AmazonS3.ImageValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCompressor {
    private static final String DEFAULT_FORMAT = "jpg";
    private static final float DEFAULT_QUALITY = 0.8f;


    private final ImageValidation imageValidation;

    public MultipartFile compressImage(MultipartFile originalFile, int size) {
        return compressImage(originalFile, size, DEFAULT_QUALITY);
    }

    public MultipartFile compressImage(MultipartFile originalFile, int size, float quality) {
        imageValidation.checkIsImage(originalFile);

        String format = getFormatName(originalFile.getOriginalFilename());

        try (InputStream inputStream = originalFile.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.of(inputStream)
                    .size(size, size)
                    .outputFormat(format)
                    .outputQuality(quality)
                    .toOutputStream(outputStream);

            return new CustomMultipartFile(
                    originalFile.getName(),
                    originalFile.getOriginalFilename(),
                    originalFile.getContentType(),
                    outputStream.toByteArray()
            );
        } catch (IOException e) {
            log.error("couldn't process the image {}", originalFile.getOriginalFilename(), e);
            throw new ImageProcessingException("couldn't process the image");
        }
    }

    private String getFormatName(String filename) {
        if (filename == null) {
            return DEFAULT_FORMAT;
        }
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return extension.matches("png|jpeg|jpg|gif|bmp") ? extension : DEFAULT_FORMAT;
    }
}