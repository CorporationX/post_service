package faang.school.postservice.service.resource;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class ResizeService {

    @Value("${spring.resources.image.max-width-horizontal}")
    private int maxWidthHorizontal;

    @Value("${spring.resources.image.max-height-horizontal}")
    private int maxHeightHorizontal;

    @Value("${spring.resources.image.max-size-square}")
    private int maxSizeSquare;

    public MultipartFile resizeImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Unsupported image format");
        }

        if (needsResizing(originalImage)) {
            ByteArrayOutputStream resizedImageOutputStream = new ByteArrayOutputStream();
            BufferedImage resizedImage = applyResize(originalImage);
            ImageIO.write(resizedImage, "png", resizedImageOutputStream);
            byte[] resizedImageBytes = resizedImageOutputStream.toByteArray();

            return createMultipartFile(file, resizedImageBytes);
        }
        return file;
    }

    private boolean needsResizing(BufferedImage image) {
        return image.getWidth() > maxWidthHorizontal || image.getHeight() > maxHeightHorizontal ||
                image.getWidth() > maxSizeSquare || image.getHeight() > maxSizeSquare;
    }

    private BufferedImage applyResize(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        double aspectRatio = (double) width / height;

        int newWidth = getNewWidth(aspectRatio);
        int newHeight = getNewHeight(aspectRatio);

        if (newWidth > maxSizeSquare || newHeight > maxSizeSquare) {
            newWidth = newHeight = maxSizeSquare;
        }

        return Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .asBufferedImage();
    }

    private MultipartFile createMultipartFile(MultipartFile originalFile, byte[] resizedImage) {
        return new CustomMultipartFile(
                resizedImage,
                originalFile.getOriginalFilename(),
                originalFile.getContentType(),
                originalFile.getName()
        );
    }

    private int getNewHeight(double aspectRatio) {
        if (aspectRatio > 1) {
            return (int) (maxHeightHorizontal / aspectRatio);
        } else {
            return maxHeightHorizontal;
        }
    }

    private int getNewWidth(double aspectRatio) {
        if (aspectRatio > 1) {
            return maxWidthHorizontal;
        } else {
            return (int) (maxHeightHorizontal * aspectRatio);
        }
    }

    public ByteArrayOutputStream squeezeImageOrLeave(BufferedImage originalImage, int maxSize) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        ByteArrayOutputStream resizedImage = new ByteArrayOutputStream();

        try {
            Thumbnails.Builder<BufferedImage> thumbnailBuilder = Thumbnails.of(originalImage);

            if (originalWidth > maxSize || originalHeight > maxSize) {
                thumbnailBuilder
                        .size(maxSize, maxSize)
                        .keepAspectRatio(true);
            } else {
                thumbnailBuilder.scale(1);
            }

            thumbnailBuilder
                    .outputFormat("png")
                    .toOutputStream(resizedImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return resizedImage;
    }
}