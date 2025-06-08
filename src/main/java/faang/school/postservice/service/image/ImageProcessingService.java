package faang.school.postservice.service.image;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class ImageProcessingService {

    private static final int LARGE_IMAGE_MAX_SIZE = 1080;
    private static final int SMALL_IMAGE_MAX_SIZE = 170;


    public byte[] resizeImage(MultipartFile originalFile, int maxSize) throws IOException {
        String ct = originalFile.getContentType();
        if (ct == null || !ct.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("This file is not an image");
        }

        BufferedImage originalImage = ImageIO.read(originalFile.getInputStream());
        if (originalImage == null) {
            throw new IOException("Cannot read image from MultipartFile");
        }

        String originalName = originalFile.getOriginalFilename();
        String formatName = (originalName != null && originalName.contains("."))
                ? getFileExtension(originalName.toLowerCase())
                : "png";

        if (!isImageFormatSupported(formatName)) {
            formatName = "jpeg";
            log.warn("Unsupported format \"{}\" for file {}. Defaulting to JPEG.",
                    formatName, originalName);
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .size(maxSize, maxSize)
                .keepAspectRatio(true)
                .outputFormat(formatName)
                .toOutputStream(os);

        return os.toByteArray();
    }

    public byte[] createLargeImage(MultipartFile originalFile) throws IOException {
        return resizeImage(originalFile, LARGE_IMAGE_MAX_SIZE);
    }

    public byte[] createSmallImage(MultipartFile originalFile) throws IOException {
        return resizeImage(originalFile, SMALL_IMAGE_MAX_SIZE);
    }

    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return "png";
        }
        int idx = fileName.lastIndexOf('.');
        if (idx == -1 || idx == fileName.length() - 1) {
            return "png";
        }
        return fileName.substring(idx + 1);
    }

    public boolean isImageFormatSupported(String formatName) {
        if (formatName == null || formatName.isBlank()) {
            return false;
        }
        return ImageIO.getImageWritersByFormatName(formatName).hasNext();
    }

    public String getResizedImageContentType(String originalContentType) {
        if (originalContentType == null) {
            return "image/jpeg";
        }
        String ct = originalContentType.toLowerCase();
        if (ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/gif")) {
            return ct;
        }
        return "image/jpeg";
    }
}
