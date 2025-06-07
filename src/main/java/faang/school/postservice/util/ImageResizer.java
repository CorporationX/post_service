package faang.school.postservice.util;

import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.model.ad.PictureSize;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@UtilityClass
@Slf4j
public class ImageResizer {
    public static int SMALL_PICTURE_MAX_SIDE_PXL = 170;
    public static int LARGE_PICTURE_MAX_SIDE_PXL = 1080;
    public static final Set<String> IMAGE_FORMATS = Set.of("jpg", "jpeg", "png", "gif", "bmp", "wbmp", "tiff");

    public ByteArrayInputStream getResizedImageStream(MultipartFile file, PictureSize size) {
        validateFileType(file);
        BufferedImage image = convertFileToBufferedImage(file);
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        int targetSide;
        if (size == PictureSize.SMALL) {
            targetSide = SMALL_PICTURE_MAX_SIDE_PXL;
        }
        else {
            targetSide = LARGE_PICTURE_MAX_SIDE_PXL;
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(image)
                    .size(targetSide, targetSide)
                    .keepAspectRatio(true)
                    .outputFormat(formatName)
                    .toOutputStream(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Exception while resizing image was thrown: {}", e.getMessage(), e);
            throw new FileProcessException("Exception while resizing image was thrown");
        }
    }

    private BufferedImage convertFileToBufferedImage(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        if (!IMAGE_FORMATS.contains(formatName)) {
            log.error("Uploaded file is not a supported image format: {}", fileName);
            throw new FileProcessException("Uploaded file is not a supported image format: %s".formatted(fileName));
        }
        BufferedImage originalImage;
        try (InputStream fileInputStream = file.getInputStream()) {
            originalImage = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            log.error("IOException while converting image was thrown", e);
            throw new FileProcessException("IOException while converting image was thrown");
        }
        if (originalImage == null) {
            log.error("The uploaded file is not a valid image or an unsupported format: {}", file.getOriginalFilename());
            throw new FileProcessException("Uploaded file is not a valid image or format is not supported: %s"
                    .formatted(file.getOriginalFilename()));
        }
        return originalImage;
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new IllegalArgumentException("Incorrect file type %s".formatted(contentType));
        }
    }
}
