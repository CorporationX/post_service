package faang.school.postservice.service.resource;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ResizeService {

    private static final int MAX_WIDTH_HORIZONTAL = 1080;
    private static final int MAX_HEIGHT_HORIZONTAL = 566;
    private static final int MAX_SIZE = 1080;

    public MultipartFile resizeImage(MultipartFile file) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            int width = originalImage.getWidth();
            int height = originalImage.getHeight();

            if ((width > MAX_WIDTH_HORIZONTAL && height <= MAX_HEIGHT_HORIZONTAL) ||
                    (width > MAX_WIDTH_HORIZONTAL && height > MAX_HEIGHT_HORIZONTAL && width > height)) {
                Thumbnails.of(originalImage)
                        .size(MAX_WIDTH_HORIZONTAL, MAX_HEIGHT_HORIZONTAL)
                        .outputFormat("jpeg")
                        .toOutputStream(baos);
            } else if (width > MAX_SIZE || height > MAX_SIZE) {
                Thumbnails.of(originalImage)
                        .size(MAX_SIZE, MAX_SIZE)
                        .outputFormat("jpeg")
                        .toOutputStream(baos);
            } else {
                return file;
            }

            byte[] resizedImage = baos.toByteArray();
            return new MockMultipartFile(
                    file.getName(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    resizedImage
            );
        }
    }
}