package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ImageProcessingException;
import faang.school.postservice.model.Comment;

import faang.school.postservice.service.s3.S3StorageService;
import faang.school.postservice.service.util.ImageProcessor;
import faang.school.postservice.service.util.ProcessedImages;
import faang.school.postservice.validation.CommentValidator;
import faang.school.postservice.validation.ValidateImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

/**
 * Сервис для работы с изображениями комментариев.
 * Предоставляет методы для загрузки и удаления изображений, а также валидации данных.
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>{@link #uploadImage(Long, Long, MultipartFile)} - загрузка изображения</li>
 *   <li>{@link #deleteImage(Long, Long)} - удаление изображения</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentImageService {
    private static final String IMAGE_CONTENT_TYPE = "image/jpeg";
    private static final String KEY_TEMPLATE = "comments/%s/%s";
    private static final String LARGE_FILE_NAME_TEMPLATE = "%s-large.jpg";
    private static final String SMALL_FILE_NAME_TEMPLATE = "%s-small.jpg";

    private final CommentService commentService;
    private final S3StorageService storageService;
    private final ImageProcessor imageProcessor;
    private final ValidateImage validateImage;
    private final CommentValidator commentValidator;

    /**
     * Загружает изображение, прикрепленное к комментарию.
     *
     * @param postId    ID поста, к которому относится комментарий
     * @param commentId ID комментария для загрузки изображения
     * @param file      файл изображения
     * @return DTO комментария с обновленными ссылками на изображения
     * @throws EntityNotFoundException если комментарий или пост не найдены
     */
    @Transactional
    public CommentViewDto uploadImage(Long postId, Long commentId, MultipartFile file) {
        validateImage.validateImageFile(file);
        Comment comment = getValidatedComment(postId, commentId);

        BufferedImage originalImage = null;
        try {
            originalImage = imageProcessor.convertToBufferedImage(file);
        } catch (IOException exception) {
            log.error("Failed to convert image", exception);
            throw new ImageProcessingException("Failed to convert image");
        }

        ProcessedImages processedImages = imageProcessor.processImage(originalImage);
        String baseKey = String.format(KEY_TEMPLATE, commentId, UUID.randomUUID());
        uploadProcessedImages(baseKey, processedImages);

        return updateCommentWithImages(comment, baseKey);
    }

    /**
     * Удаляет изображения комментария и очищает ссылки на них.
     *
     * @param postId    ID поста, к которому относится комментарий
     * @param commentId ID комментария для удаления изображений
     * @return DTO комментария с очищенными ссылками на изображения
     * @throws EntityNotFoundException если комментарий или пост не найдены
     */
    @Transactional
    public CommentViewDto deleteImage(Long postId, Long commentId) {
        Comment comment = getValidatedComment(postId, commentId);

        try {
            deleteCommentImages(comment);
        } catch (ImageProcessingException exception) {
            log.warn("Failed to delete images from storage", exception);
        }

        return clearImageReferences(comment);
    }

    /**
     * Получает комментарий и проверяет его принадлежность к посту.
     *
     * @param postId    ID поста, к которому относится комментарий
     * @param commentId ID комментария для проверки
     * @return DTO комментария
     * @throws EntityNotFoundException если комментарий или пост не найдены
     */
    private Comment getValidatedComment(Long postId, Long commentId) {
        Comment comment = commentService.getCommentById(commentId);
        commentValidator.validateCommentBelongsToPost(comment, postId);
        return comment;
    }

    /**
     * Загружает обработанные изображения в S3-хранилище.
     *
     * @param baseKey базовый ключ для формирования имен файлов
     * @param images  объект ProcessedImages с изображениями
     */
    private void uploadProcessedImages(String baseKey, ProcessedImages images) {
        try {
            storageService.uploadToS3(
                    String.format(LARGE_FILE_NAME_TEMPLATE, baseKey),
                    images.largeImage(),
                    IMAGE_CONTENT_TYPE
            );
            storageService.uploadToS3(
                    String.format(SMALL_FILE_NAME_TEMPLATE, baseKey),
                    images.smallImage(),
                    IMAGE_CONTENT_TYPE
            );
        } catch (IOException exception) {
            log.error("Failed to upload processed images", exception);
            throw new ImageProcessingException("Failed to upload processed images");
        }
    }

    /**
     * Обновляет комментарий с новыми ссылками на изображения.
     *
     * @param comment комментарий для обновления
     * @param baseKey базовый ключ для формирования имен файлов
     * @return обновленный DTO комментария
     */
    private CommentViewDto updateCommentWithImages(Comment comment, String baseKey) {
        comment.setLargeImageFileKey(String.format(LARGE_FILE_NAME_TEMPLATE, baseKey));
        comment.setSmallImageFileKey(String.format(SMALL_FILE_NAME_TEMPLATE, baseKey));
        return commentService.updateCommentEntity(comment);
    }

    /**
     * Удаляет изображения комментария из S3-хранилища.
     *
     * @param comment комментарий для удаления изображений
     */
    private void deleteCommentImages(Comment comment) {
        if (comment.getLargeImageFileKey() != null) {
            storageService.deleteFile(comment.getLargeImageFileKey());
        }
        if (comment.getSmallImageFileKey() != null) {
            storageService.deleteFile(comment.getSmallImageFileKey());
        }
    }

    /**
     * Очищает ссылки на изображения в комментарии и обновляет его.
     *
     * @param comment комментарий для обновления
     * @return обновленный DTO комментария
     */
    private CommentViewDto clearImageReferences(Comment comment) {
        comment.setLargeImageFileKey(null);
        comment.setSmallImageFileKey(null);
        return commentService.updateCommentEntity(comment);
    }
}