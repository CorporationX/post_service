package faang.school.postservice.service.post;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DimensionChanger {

    private static final int MAX_IMAGE_HEIGHT = 1080;
    private static final int MAX_FILE_WIDTH = 566;

    protected void changeFileDimension(MultipartFile image) {
        List<byte[]> images = new ArrayList<>();
        images.add(changeFileScale(image, MAX_IMAGE_HEIGHT));
        images.add(changeFileScale(image, MAX_FILE_WIDTH));
    }

    private byte[] changeFileScale(MultipartFile image, int scale) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            BufferedImage bufferedImage = Scalr.resize(ImageIO.read(
                            image.getInputStream()),
                    Scalr.Method.QUALITY,
                    scale, scale);

            ImageIO.write(bufferedImage, "png", stream);

            return stream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("ChangerDimension. Filed to changed file size", e.getCause());
        }
    }
}
