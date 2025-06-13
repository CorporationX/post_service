package faang.school.postservice.service.image;

import faang.school.postservice.dto.image.ImageStorage;
import faang.school.postservice.exception.file.FileReadException;
import faang.school.postservice.exception.file.FileUploadException;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import faang.school.postservice.validation.image.ImageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private static final String IMAGE_FORMAT = "jpg";
    private static final int MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX = 170;
    private static final int MAX_SIDE_SIZE_OF_IMAGE_PX = 1080;

    private final ImageValidator imageValidator;
    private final S3Service s3Service;
    private final S3KeyGenerator s3KeyGenerator;

    public ImageStorage uploadToS3(MultipartFile file) {
        imageValidator.validate(file);

        String imageKey = s3KeyGenerator.generateImageKey(file.getOriginalFilename());
        String previewKey = s3KeyGenerator.generatePreviewKey(imageKey);

        processAndUploadImages(file, imageKey, previewKey);

        return new ImageStorage(imageKey, previewKey, file.getContentType(), file.getSize());
    }

    public Resource download(String key) {
        return s3Service.download(key);
    }

    public void delete(String key) {
        s3Service.delete(key);
    }

    private void processAndUploadImages(MultipartFile file, String imagePath, String previewPath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new FileReadException("Файл не является изображением");
            }

            byte[] largeImageBytes = resizeImageToBytes(bufferedImage, MAX_SIDE_SIZE_OF_IMAGE_PX);
            byte[] smallImageBytes = resizeImageToBytes(bufferedImage, MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX);

            s3Service.upload(largeImageBytes, imagePath, file.getContentType());
            s3Service.upload(smallImageBytes, previewPath, file.getContentType());
        } catch (FileReadException e) {
            log.warn("Ошибка при считывании изображения!", e);
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при обработке изображения!", e);
            throw new FileUploadException("Ошибка при обработке изображения");
        }
    }


    private byte[] resizeImageToBytes(BufferedImage sourceImage, int maxSideSizePx) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(sourceImage)
                    .size(maxSideSizePx, maxSideSizePx)
                    .outputFormat(IMAGE_FORMAT)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ошибка при сжатии изображения!", e);
            throw new FileUploadException("Ошибка при ресайзе изображения");
        }
    }
}
