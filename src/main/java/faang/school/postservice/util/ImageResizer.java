package faang.school.postservice.util;

import faang.school.postservice.exception.FileProcessException;
import faang.school.postservice.model.ad.PictureSize;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ImageResizer {
    public static int SMALL_PICTURE_MAX_SIDE_PXL = 170;
    public static int LARGE_PICTURE_MAX_SIDE_PXL = 1080;

    public ByteArrayInputStream getResizedImageStream(MultipartFile file, PictureSize size) {
        validateFileType(file);
        BufferedImage image = convertFileToBufferedImage(file);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int targetSide;
        if (size == PictureSize.SMALL) {
            targetSide = SMALL_PICTURE_MAX_SIDE_PXL;
        }
        else {
            targetSide = LARGE_PICTURE_MAX_SIDE_PXL;
        }
        try {
            Thumbnails.of(image)
                    .size(targetSide, targetSide)
                    .keepAspectRatio(true)
                    .toOutputStream(outputStream);
        } catch (Exception e) {
            log.error("Exception while resizing image was thrown", e);
            throw new FileProcessException("Exception while resizing image was thrown");
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private BufferedImage convertFileToBufferedImage(MultipartFile file) {
        String fileName = file.getName();
        String formatName = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        List<String> formatList = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "wbmp", "tiff");
//        позволяет быстро отсеять файлы с неподдерживаемыми расширениями,
//        что может сэкономить ресурсы, если файл не соответствует ожидаемому формату
        if (!formatList.contains(formatName)) {
            log.error("Uploaded file is not a supported image format: {}", fileName);
            throw new FileProcessException("Uploaded file is not a supported image format: %s".formatted(fileName));
        }
        BufferedImage originalImage;
        try (InputStream fileInputStream = file.getInputStream()) {
            originalImage = ImageIO.read(fileInputStream);
        } catch (IOException e) {
            log.error("IOException while converting image was thrown", e);
            throw new FileProcessException("IOException while converting image was thrown");
        }
        if (originalImage == null) {
            log.error("The uploaded file is not a valid image or an unsupported format: {}", file.getOriginalFilename());
            throw new FileProcessException("Uploaded file is not a valid image or format is not supported: %s"
                    .formatted(file.getOriginalFilename()));
        }
//        Обеспечивает фактическую валидацию содержимого файла, что файл действительно является изображением.
//        Например, файл с расширением .jpg может быть поврежден или не содержать изображение
        return originalImage;
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image")) {
            throw new IllegalArgumentException("Incorrect file type %s".formatted(contentType));
        }
    }
}
