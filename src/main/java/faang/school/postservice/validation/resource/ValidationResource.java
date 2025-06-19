package faang.school.postservice.validation.resource;

import faang.school.postservice.exception.DataValidationException;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class ValidationResource {
    private static final long MAX_FILE_SIZE_IN_BYTES = 5 * 1024 * 1024;

    public MultipartFile resizeImageIfNeeded(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        checkPictureWeight(file);
        BufferedImage resultImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT,
                bufferedImage.getWidth(), bufferedImage.getHeight());

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resultImage, "jpg", byteArrayOutputStream);
            byte[] byteImage = byteArrayOutputStream.toByteArray();

            return new InMemoryMultipartFile(
                    "file",
                    file.getOriginalFilename(),
                    "image/jpg",
                    byteImage
            );
        }
    }

    private void checkPictureWeight(MultipartFile file) {
        long sizeFile = file.getSize();
        if (sizeFile > MAX_FILE_SIZE_IN_BYTES) {
            throw new DataValidationException("Maximum file size exceeded " + MAX_FILE_SIZE_IN_BYTES);
        }
    }
}