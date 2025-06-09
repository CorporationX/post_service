package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.file.FileNotFoundException;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.model.comment.CommentImage;
import faang.school.postservice.repository.comment.CommentImageRepository;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.image.ImageService;
import faang.school.postservice.validation.image.CommentImageFileValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

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

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found"));

        ImageResponseDto imageDto = imageService.uploadToS3(file);

        CommentImage image = CommentImage.builder()
                .commentId(comment.getId())
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
        InputStream stream = imageService.download(image.getFileKey());
        Resource resource = new InputStreamResource(stream);

        return new ImageDownloadDto(resource, image.getFileKey(), image.getContentType());
    }

    public ImageDownloadDto downloadPreviewByCommentId(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        InputStream stream = imageService.download(image.getPreviewKey());
        Resource resource = new InputStreamResource(stream);

        return new ImageDownloadDto(resource, image.getFileKey(), image.getContentType());
    }

    public void deleteImage(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        imageService.delete(image.getFileKey());
        commentImageRepository.delete(image);
    }

    public MediaType getContentType(Long commentId, Long imageId) {
        CommentImage image = getCommentImageByIdOrThrow(commentId, imageId);
        return imageService.detectContentType(image.getFileKey());
    }

    private CommentImage getCommentImageByIdOrThrow(Long commentId, Long imageId) {
        return commentImageRepository.findByIdAndCommentId(imageId, commentId)
                .orElseThrow(() -> new FileNotFoundException("Изображение не найдено для комментария " + commentId));
    }
}
