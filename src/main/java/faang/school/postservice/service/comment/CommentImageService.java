package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.validation.image.CommentImageFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentImageService {

    private final ImageService imageService;
    private final CommentImageFileValidator imageFileValidator;
    private final CommentRepository commentRepository;
    private final CommentImageRepository commentImageRepository;
    private final UserContext userContext;

    public ImageResponseDto uploadImageForComment(Long commentId, MultipartFile file) {
        imageFileValidator.validate(file);

        if (!commentRepository.existsById(commentId)) {
            throw new CommentNotFoundException("Комментарий не найден!");
        }

        ImageResponseDto imageDto = imageService.uploadToS3(file);

        CommentImage image = CommentImage.builder()
                .commentId(commentId)
                .userId(userContext.getUserId())
                .fileKey(imageDto.getFileKey())
                .previewKey(imageDto.getPreviewKey())
                .contentType(imageDto.getContentType())
                .size(imageDto.getSize())
                .build();

        commentImageRepository.save(image);

        return imageDto;
    }

    public ImageDownloadDto downloadImageByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        Resource resource = imageService.download(image.getFileKey());

        return new ImageDownloadDto(resource, image.getFileKey(), image.getContentType());
    }

    public ImageDownloadDto downloadPreviewByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        Resource resource = imageService.download(image.getPreviewKey());

        return new ImageDownloadDto(resource, image.getFileKey(), image.getContentType());
    }

    public void deleteImage(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        imageService.delete(image.getFileKey());
        commentImageRepository.delete(image);
    }

    private CommentImage getCommentImageByIdOrThrow(Long commentId, Long imageId) {
        return commentImageRepository.findByIdAndCommentId(imageId, commentId)
                .orElseThrow(() -> new FileNotFoundException(
                        String.format("Изображение не найдено для комментария %d!", commentId)
                ));
    }
}
