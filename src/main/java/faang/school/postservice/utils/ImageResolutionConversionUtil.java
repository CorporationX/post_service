package faang.school.postservice.utils;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.exception.ResizeFileException;
import faang.school.postservice.exception.UnsupportedResourceException;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class ImageResolutionConversionUtil {

private static final int MAX_IMAGE_WIDTH_PX = 1024;
private static final int MAX_IMAGE_HEIGHT_PX = 566;
private static final int EXTENSION_START_INDEX = 1;
private static final double OUTPUT_IMAGE_QUALITY = 0.8d;

    public MultipartFile compressImage(MultipartFile file) {
        try {
            log.debug("Starting image resolution conversion for file: {}", file.getOriginalFilename());
            InputStream inputStream = file.getInputStream();
            BufferedImage image = ImageIO.read(inputStream);

            if (image == null) {
                log.error("File is not an image: {}", file.getOriginalFilename());
                throw new UnsupportedResourceException(String.format("File is not an image: %s", file.getOriginalFilename()));
            }

            int width = image.getWidth();
            int height = image.getHeight();
            String extension = getResourceExtension(file);
            log.debug("Taking image with resolution {}x{}", width, height);

            byte[] resizedImageBytes = findResourceOrientation(image, width, height, extension);
            log.debug("Image resolution conversion completed for file: {}", file.getOriginalFilename());

            return CustomMultipartFile.builder()
                    .name(file.getName())
                    .originalFilename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .content(resizedImageBytes)
                    .build();

        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            throw new FileException("Error reading file: " + e.getMessage());
        }
    }

    private byte[] findResourceOrientation(BufferedImage image, int width, int height, String extension) {
        byte[] resizedImageBytes;
        if (width > height) {
            log.debug("Image width: {}, height: {}, image is horizontal", width, height);
            resizedImageBytes = resizeImage(image, MAX_IMAGE_WIDTH_PX, MAX_IMAGE_HEIGHT_PX, extension);
        } else if (width < height) {
            log.debug("Image width: {}, height: {}, image is vertical", width, height);
            resizedImageBytes = resizeImage(image, MAX_IMAGE_WIDTH_PX, MAX_IMAGE_HEIGHT_PX, extension);
        } else {
            log.debug("Image width: {}, height: {}, image is square", width, height);
            resizedImageBytes = resizeImage(image, MAX_IMAGE_WIDTH_PX, MAX_IMAGE_WIDTH_PX, extension);
        }
        return resizedImageBytes;
    }

    private byte[] resizeImage(BufferedImage image, int targetWidth, int targetHeight, String extension) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(image)
                    .size(targetWidth, targetHeight)
                    .outputQuality(OUTPUT_IMAGE_QUALITY)
                    .outputFormat(extension)
                    .toOutputStream(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error resizing image: {}", e.getMessage(), e);
            throw new ResizeFileException("Error resizing image: " + e.getMessage());
        }
    }

    private String getResourceExtension(MultipartFile file) {
        MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
        try {
            MimeType mimeType = mimeTypes.forName(file.getContentType());
            return mimeType.getExtension().substring(EXTENSION_START_INDEX);
        } catch (MimeTypeException e) {
            throw new IllegalStateException(String.format("Failed to determine file extension for file: %s", file.getOriginalFilename()), e);
        }
    }
}
