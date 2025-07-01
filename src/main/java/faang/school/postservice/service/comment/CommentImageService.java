package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.dto.image.ImageResource;
import faang.school.postservice.entity.resource.Resource;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.model.resource.ImageResources;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.service.resource.ResourceService;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentImageService {

    private final ImageService imageService;
    private final ResourceService resourceService;
    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;

    @Transactional
    public ImageResponseDto uploadImageForComment(Long commentId, MultipartFile file) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentValidator.validateCommentAuthor(comment.getAuthorId());
        ImageResource imageStorage = imageService.uploadToS3(file);
        ImageResources resources = resourceService.uploadImageResources(imageStorage);

        comment.setLargeImageResource(resources.original());
        comment.setSmallImageResource(resources.preview());

        commentRepository.save(comment);

        log.info("Для комментария с id={} загружено изображение с fileName={}", commentId, file.getOriginalFilename());
        return new ImageResponseDto(
                imageStorage.fileKey(),
                imageStorage.previewKey(),
                imageStorage.contentType(),
                imageStorage.size()
        );
    }

    @Transactional(readOnly = true)
    public ImageDownloadDto downloadImageByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        Resource imageResource = comment.getLargeImageResource();
        org.springframework.core.io.Resource streamResource = imageService.download(imageResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(imageResource.getType());
        log.info("Получено изображение комментария с id={} и resourceId={}", commentId, imageResource.getId());
        return new ImageDownloadDto(streamResource, imageResource.getName(), mediaType);
    }

    @Transactional(readOnly = true)
    public ImageDownloadDto downloadPreviewByCommentId(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        Resource previewResource = comment.getSmallImageResource();
        org.springframework.core.io.Resource streamResource = imageService.download(previewResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(previewResource.getType());
        log.info("Получено превью изображения комментария с id={} и imageId={}", commentId, previewResource.getId());
        return new ImageDownloadDto(streamResource, previewResource.getName(), mediaType);
    }

    @Transactional
    public void deleteImage(Long commentId, Long imageId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentValidator.validateCommentAuthor(comment.getAuthorId());

        Resource imageResource = comment.getLargeImageResource();
        Resource previewResource = comment.getSmallImageResource();

        imageService.delete(imageResource.getKey());
        imageService.delete(previewResource.getKey());

        resourceService.deleteResource(imageResource);
        resourceService.deleteResource(previewResource);

        log.info("Удалено изображение с комментария с id={} и imageId={}", commentId, imageId);
    }
}
