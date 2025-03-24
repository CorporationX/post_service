package faang.school.postservice.service.post;

import dev.mccue.imgscalr.Scalr;
import faang.school.postservice.utils.ByteArrayMultipartFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PostImageService {

    @Value("${cover.max-image-width}")
    protected int maxWidth;

    @Value("${cover.max-image-height-horizontal}")
    protected int maxHeightHorizontal;

    public MultipartFile getResizedCover(MultipartFile cover) {
        try {
            BufferedImage image = ImageIO.read(cover.getInputStream());
            BufferedImage resizedCover = Scalr.resize(
                    image,
                    Scalr.Method.QUALITY,
                    Scalr.Mode.AUTOMATIC,
                    maxWidth,
                    calculateNewHeight(image));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedCover, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return new ByteArrayMultipartFile(imageBytes, cover.getOriginalFilename(), cover.getContentType());
        } catch (IOException e) {
            log.error("Ошибка форматирования изображения: {}", cover.getName());
            throw new RuntimeException(e);
        }

    }

    public int calculateNewHeight(BufferedImage image) {
        double aspectRatio = (double) image.getHeight() / image.getWidth();
        return image.getWidth() > image.getHeight()
                ? (int) (maxWidth * aspectRatio)
                : maxHeightHorizontal;
    }

}
