package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.dto.image.ImageStorage;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.ImageResource;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentImageService {

    private final ImageService imageService;
    private final ResourceService resourceService;
    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final CommentImageRepository commentImageRepository;

    public ImageResponseDto uploadImageForComment(Long commentId, MultipartFile file) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentValidator.validateCommentAuthor(comment.getAuthorId());

        ImageStorage imageStorage = imageService.uploadToS3(file);

        ImageResource imageResource = ImageResource.builder()
                .key(imageStorage.fileKey())
                .name(file.getOriginalFilename())
                .type(imageStorage.contentType())
                .size(imageStorage.size())
                .build();
        ImageResource savedImage = resourceService.saveResource(imageResource);

        ImageResource previewResource = ImageResource.builder()
                .key(imageStorage.previewKey())
                .name("preview_" + file.getOriginalFilename())
                .type(imageStorage.contentType())
                .size(imageStorage.size())
                .build();
        ImageResource savedPreview = resourceService.saveResource(previewResource);

        CommentImage image = CommentImage.builder()
                .comment(comment)
                .image(savedImage)
                .preview(savedPreview)
                .build();

        commentImageRepository.save(image);

        log.info("Для комментария с id={} загружено изображение imageId={}", commentId, image.getId());
        return new ImageResponseDto(
                image.getId(),
                imageStorage.fileKey(),
                imageStorage.previewKey(),
                imageStorage.contentType(),
                imageStorage.size()
        );
    }

    public ImageDownloadDto downloadImageByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        ImageResource imageResource = image.getImage();
        Resource streamResource = imageService.download(imageResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(imageResource.getType());
        log.info("Получено изображение комментария с id={} и imageId={}", commentId, image.getId());
        return new ImageDownloadDto(image.getId(), streamResource, imageResource.getName(), mediaType);
    }

    public ImageDownloadDto downloadPreviewByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        ImageResource previewResource = image.getPreview();
        Resource streamResource = imageService.download(previewResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(previewResource.getType());
        log.info("Получено превью изображения комментария с id={} и imageId={}", commentId, image.getId());
        return new ImageDownloadDto(image.getId(), streamResource, previewResource.getName(), mediaType);
    }

    public void deleteImage(Long commentId, Long imageId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentValidator.validateCommentAuthor(comment.getAuthorId());

        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);

        ImageResource imageResource = image.getImage();
        ImageResource previewResource = image.getPreview();

        imageService.delete(imageResource.getKey());
        imageService.delete(previewResource.getKey());

        commentImageRepository.delete(image);

        resourceService.deleteResource(imageResource);
        resourceService.deleteResource(previewResource);

        log.info("Удалено изображение с комментария с id={} и imageId={}", commentId, image.getId());
    }

    private CommentImage getCommentImageByIdOrThrow(Long commentId, Long imageId) {
        return commentImageRepository.findByIdAndCommentId(imageId, commentId)
                .orElseThrow(() -> new FileNotFoundException(
                        String.format("Изображение не найдено для комментария %d!", commentId)
                ));
    }
}
