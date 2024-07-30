package faang.school.postservice.service.resource;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ResizeService {

    @Value("${spring.resources.image.max-width-horizontal}")
    private int maxWidthHorizontal;

    @Value("${spring.resources.image.max-height-horizontal}")
    private int maxHeightHorizontal;

    @Value("${spring.resources.image.max-size-square}")
    private int MaxSizeSquare;

    public MultipartFile resizeImage(MultipartFile file) throws IOException {
        try (ByteArrayOutputStream resizedImageOutputStream  = new ByteArrayOutputStream()) {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if ((width > maxWidthHorizontal && height <= maxHeightHorizontal) ||
                    (width > maxWidthHorizontal && height > maxHeightHorizontal && width > height)) {
                Thumbnails.of(originalImage)
                        .size(maxWidthHorizontal, maxHeightHorizontal)
                        .outputFormat("jpeg")
                        .toOutputStream(resizedImageOutputStream);
            } else if (width > MaxSizeSquare || height > MaxSizeSquare) {
                Thumbnails.of(originalImage)
                        .size(MaxSizeSquare, MaxSizeSquare)
                        .outputFormat("jpeg")
                        .toOutputStream(resizedImageOutputStream);
            } else {
                return file;
            }

            byte[] resizedImage = resizedImageOutputStream.toByteArray();
            return new CustomMultipartFile(
                    file.getName(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    resizedImage
            );
        }
    }
}