package faang.school.postservice.validation;

import faang.school.postservice.util.ImageCompressor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class MultipartFileValidator {

    private final ImageCompressor imageCompressor;

    private static final long MAX_SIZE = 5 * 1024 * 1024;
    private static final int MAX_WIDTH_LANDSCAPE = 1080;
    private static final int MAX_HEIGHT_LANDSCAPE = 566;
    private static final int MAX_SIZE_SQUARE = 1080;

    public void validateFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            if (file.getSize() > MAX_SIZE) {
                throw new IllegalArgumentException();
            }

            if (Objects.requireNonNull(file.getContentType()).startsWith("image") ) {
                validateImage(file);
//            } else if (!file.getContentType().startsWith("video")
//                    || !file.getContentType().startsWith("audio")) {
//
//                throw new IllegalArgumentException("Incorrect content type");
            }
        }
    }

    public void validateImage(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image.getWidth() > MAX_WIDTH_LANDSCAPE &&
                    (image.getHeight() > MAX_SIZE_SQUARE || image.getHeight() > MAX_HEIGHT_LANDSCAPE)) {

                file = imageCompressor.compressImage(image, file);

            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
