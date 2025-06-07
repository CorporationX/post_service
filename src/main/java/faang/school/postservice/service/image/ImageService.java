package faang.school.postservice.service.image;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.exception.file.FileReadException;
import faang.school.postservice.exception.file.FileUploadException;
import faang.school.postservice.model.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.validation.image.ImageFileValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String IMAGE_FORMAT = "jpg";
    private static final int MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX = 170;
    private static final int MAX_SIDE_SIZE_OF_IMAGE_PX = 1080;

    private final CommentImageRepository imageRepository;
    private final ImageFileValidator imageFileValidator;
    private final UserContext userContext;
    private final S3Service s3Service;
    private final S3KeyGenerator s3KeyGenerator;

    @SneakyThrows
    public ImageResponseDto uploadImage(MultipartFile file) {
        imageFileValidator.validate(file);

        String imageKey = s3KeyGenerator.generateImageKey(file.getOriginalFilename());
        String previewKey = s3KeyGenerator.generatePreviewKey(imageKey);

        processAndUploadImages(file, imageKey, previewKey);
        CommentImage savedImage = saveImageMetadata(file, imageKey, previewKey);

        return new ImageResponseDto(
                savedImage.getFileKey(),
                savedImage.getPreviewKey(),
                savedImage.getContentType(),
                savedImage.getSize());
    }

    @SneakyThrows
    public ImageDownloadDto downloadImageById(Long imageId) {
        CommentImage image = imageRepository.getByIdOrThrow(imageId);
        InputStream stream = s3Service.download(image.getFileKey());

        return new ImageDownloadDto(stream, image.getContentType(), image.getFileKey());
    }

    @SneakyThrows
    public ImageDownloadDto downloadPreviewById(Long imageId) {
        CommentImage image = imageRepository.getByIdOrThrow(imageId);
        InputStream stream = s3Service.download(image.getPreviewKey());

        return new ImageDownloadDto(stream, image.getContentType(), image.getPreviewKey());
    }

    public MediaType detectContentType(Long imageId) {
        try {
            CommentImage image = imageRepository.getByIdOrThrow(imageId);
            String contentType = s3Service.getContentType(image.getFileKey());

            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            log.warn("Не удалось определить content-type файла {}: {}", imageId, e.getMessage());
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    public void deleteImage(Long imageId) {
        CommentImage image = imageRepository.getByIdOrThrow(imageId);
        s3Service.delete(image.getFileKey());
    }

    @Async
    @Transactional
    private void processAndUploadImages(MultipartFile file, String imagePath, String previewPath) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                throw new FileReadException("Файл не является изображением");
            }

            byte[] largeImageBytes = resizeImageToBytes(bufferedImage, MAX_SIDE_SIZE_OF_IMAGE_PX);
            byte[] smallImageBytes = resizeImageToBytes(bufferedImage, MAX_SIDE_SIZE_OF_SMALL_IMAGE_PX);

            s3Service.uploadImageBytesInS3(largeImageBytes, imagePath);
            s3Service.uploadImageBytesInS3(smallImageBytes, previewPath);
        } catch (Exception e) {
            throw new FileUploadException("Ошибка при обработке изображения");
        }
    }

    private CommentImage saveImageMetadata(MultipartFile file, String imagePath, String previewPath) {
        CommentImage image = CommentImage.builder()
                .userId(userContext.getUserId())
                .fileKey(imagePath)
                .previewKey(previewPath)
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        return imageRepository.save(image);
    }

    @SneakyThrows
    private byte[] resizeImageToBytes(BufferedImage sourceImage, int maxSideSizePx) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(sourceImage)
                    .size(maxSideSizePx, maxSideSizePx)
                    .outputFormat(IMAGE_FORMAT)
                    .toOutputStream(outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new FileUploadException("Ошибка при ресайзе изображения");
        }
    }
}
