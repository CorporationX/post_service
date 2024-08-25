package faang.school.postservice.util;

import faang.school.postservice.exceptions.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Slf4j
@Component
public class ImageCompressor {
    @Value("${services.s3.imageParameters.maxWidthHorizontal}")
    private int maxWidthHorizontal;
    @Value("${services.s3.imageParameters.maxHeightHorizontal}")
    private int maxHeightHorizontal;
    @Value("${services.s3.imageParameters.maxSizeSquare}")
    private int maxSizeSquare;

    public BufferedImage compressImage(BufferedImage originalImage) {
        log.info("Start compress image | ImageCompressor-compressImage");
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        BufferedImage compressedImage = originalImage;

        if (width == height) {
            if (width > maxSizeSquare) {
                compressedImage = getResize(originalImage, maxSizeSquare, maxSizeSquare);
            }
        } else {
            if (width > maxWidthHorizontal || height > maxHeightHorizontal) {
                compressedImage = getResize(originalImage, maxWidthHorizontal, maxHeightHorizontal);
            }
        }

        log.info("Image compression completed | ImageCompressor-compressImage");
        return compressedImage;
    }

    private BufferedImage getResize(BufferedImage originalImage, int width, int height) {
        return Scalr.resize(originalImage,
                Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.AUTOMATIC,
                width, height);
    }

    public String getImageFormat(String fileName) {
        if(fileName == null || fileName.isBlank()) {
            throw new DataValidationException("File name is empty or nul");
        }

        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < fileName.length() - 1) {
            String format = fileName.substring(lastIndexOfDot + 1).toLowerCase();
            return format;
        } else {
            throw new DataValidationException("File doesn't have format");
        }


    }
}
