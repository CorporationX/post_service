package faang.school.postservice.util.resource;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
@Slf4j
public class ImageResizer {

    @Value("${resources.image.max-width-horizontal}")
    private int maxWidthHorizontal;

    @Value("${resources.image.max-height-horizontal}")
    private int maxHeightHorizontal;

    @Value("${resources.image.max-size-square}")
    private int maxSizeSquare;

    public MultipartFile resizeImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new IOException("Failed to read image. The file might be corrupted or in an unsupported format.");
        }

        if (isResizingRequired(originalImage)) {
            BufferedImage resizedImage = applyResizing(originalImage);
            byte[] resizedImageBytes = convertImageToBytes(resizedImage);

            return createMultipartFile(file, resizedImageBytes);
        }
        return file;
    }

    private boolean isResizingRequired(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        return width > maxWidthHorizontal || height > maxHeightHorizontal || width > maxSizeSquare || height > maxSizeSquare;
    }

    private BufferedImage applyResizing(BufferedImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        double aspectRatio = (double) width / height;

        int newWidth, newHeight;
        if (aspectRatio > 1) {
            newWidth = Math.min(maxWidthHorizontal, width);
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newHeight = Math.min(maxHeightHorizontal, height);
            newWidth = (int) (newHeight * aspectRatio);
        }

        if (newWidth > maxSizeSquare || newHeight > maxSizeSquare) {
            newWidth = newHeight = maxSizeSquare;
        }

        return Thumbnails.of(originalImage)
                .size(newWidth, newHeight)
                .asBufferedImage();
    }

    private byte[] convertImageToBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }

    private MultipartFile createMultipartFile(MultipartFile originalFile, byte[] resizedImageBytes) {
        return new MultipartFileCreator(
                originalFile.getName(),
                originalFile.getOriginalFilename(),
                originalFile.getContentType(),
                resizedImageBytes
        );
    }
}
