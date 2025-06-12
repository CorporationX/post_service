package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.ImageResource;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentImageService {

    private final ImageService imageService;
    private final CommentValidator commentValidator;
    private final CommentRepository commentRepository;
    private final CommentImageRepository commentImageRepository;

    public ImageResponseDto uploadImageForComment(Long commentId, MultipartFile file) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        commentValidator.validateCommentAuthor(comment.getAuthorId());

        ImageResponseDto imageDto = imageService.uploadToS3(file);

        ImageResource imageResource = ImageResource.builder()
                .key(imageDto.getFileKey())
                .name(file.getOriginalFilename())
                .type(imageDto.getContentType())
                .size(imageDto.getSize())
                .build();
        ImageResource savedImage = imageService.saveResource(imageResource);

        ImageResource previewResource = ImageResource.builder()
                .key(imageDto.getPreviewKey())
                .name("preview_" + file.getOriginalFilename())
                .type(imageDto.getContentType())
                .size(imageDto.getSize())
                .build();
        ImageResource savedPreview = imageService.saveResource(previewResource);

        CommentImage image = CommentImage.builder()
                .comment(comment)
                .image(savedImage)
                .preview(savedPreview)
                .build();

        commentImageRepository.save(image);

        return imageDto;
    }


    public ImageDownloadDto downloadImageByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        ImageResource imageResource = image.getImage();
        Resource streamResource = imageService.download(imageResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(imageResource.getType());
        return new ImageDownloadDto(streamResource, imageResource.getName(), mediaType);
    }

    public ImageDownloadDto downloadPreviewByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        ImageResource previewResource = image.getPreview();
        Resource streamResource = imageService.download(previewResource.getKey());

        MediaType mediaType = MediaType.parseMediaType(previewResource.getType());
        return new ImageDownloadDto(streamResource, previewResource.getName(), mediaType);
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

        imageService.deleteResource(imageResource);
        imageService.deleteResource(previewResource);
    }

    private CommentImage getCommentImageByIdOrThrow(Long commentId, Long imageId) {
        return commentImageRepository.findByIdAndCommentId(imageId, commentId)
                .orElseThrow(() -> new FileNotFoundException(
                        String.format("Изображение не найдено для комментария %d!", commentId)
                ));
    }
}
