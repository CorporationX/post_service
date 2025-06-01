package faang.school.postservice.service.resource.upload;

import faang.school.postservice.config.resource.ResourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Component
public class ImageUploadStrategy extends AbstractAsyncUploadStrategy implements UploadStrategy {

    public ImageUploadStrategy(S3AsyncClient s3AsyncClient, ResourceProperties resourceProperties) {
        super(s3AsyncClient, resourceProperties.getImage());
    }

    @Override
    protected byte[] processFile(MultipartFile file) throws Exception {
        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage resized = resizeIfNeeded(image);
        return toByteArray(resized, getExtension(file));
    }

    @Override
    protected String resolveContentType(MultipartFile file) {
        return "image/jpeg";
    }

    @Override
    public String getType() {
        return "IMAGE";
    }

    private BufferedImage resizeIfNeeded(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean isSquare = width == height;

        ResourceProperties.Dimension dim = isSquare
                ? config.getResize().getSquare()
                : config.getResize().getHorizontal();

        if ((isSquare && width > dim.getWidth()) ||
                (!isSquare && (width > dim.getWidth() || height > dim.getHeight()))) {
            return resize(image, dim.getWidth(), dim.getHeight());
        }
        return image;
    }

    private BufferedImage resize(BufferedImage image, int maxWidth, int maxHeight) {
        double scale = Math.min((double) maxWidth / image.getWidth(), (double) maxHeight / image.getHeight());
        int newW = (int) (image.getWidth() * scale);
        int newH = (int) (image.getHeight() * scale);
        Image tmp = image.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.drawImage(tmp, 0, 0, null);
        g.dispose();
        return resized;
    }

    private byte[] toByteArray(BufferedImage image, String extension) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, extension, baos);
        return baos.toByteArray();
    }
}
