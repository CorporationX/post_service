package faang.school.postservice.util;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Slf4j
@Component
public class ImageCompressor {
    @Value("${services.s3.photoStandards.maxWidthHorizontal}")
    private int maxWidthHorizontal;
    @Value("${services.s3.photoStandards.maxHeightHorizontal}")
    private int maxHeightHorizontal;
    @Value("${services.s3.photoStandards.maxSizeSquare}")
    private int maxSizeSquare;

    public BufferedImage compressImage(BufferedImage originalImage) {
        log.info("Start compress image.");
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        BufferedImage scaledImage = originalImage;

        if (width > height) {
            if (width > maxWidthHorizontal || height > maxHeightHorizontal) {
                scaledImage = Scalr.resize(originalImage,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.AUTOMATIC,
                        1080, 556);
            }
        } else if (width == height) {
            if (width > maxSizeSquare || height > maxSizeSquare) {
                scaledImage = Scalr.resize(originalImage,
                        Scalr.Method.QUALITY,
                        Scalr.Mode.AUTOMATIC,
                        1080, 1080);
            }
        }
        log.info("Compression completed.");
        return scaledImage;
    }
}
