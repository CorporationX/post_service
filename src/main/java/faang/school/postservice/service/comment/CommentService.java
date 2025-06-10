package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.CommentDto;
import faang.school.postservice.exception.CommentImageException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.image.ImageProcessingService;
import faang.school.postservice.service.s3.S3StorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final S3StorageService s3StorageService;
    private final ImageProcessingService imageService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createCommentWithOptionalImage(
            String content,
            Long authorId,
            Long postId,
            MultipartFile file
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        Comment comment = Comment.builder()
                .content(content)
                .authorId(authorId)
                .post(post)
                .build();

        try {
            comment = commentRepository.save(comment);
            if (file != null && !file.isEmpty()) {
                attachImageToComment(comment, file);
                comment = commentRepository.save(comment);
            }
            CommentDto dto = commentMapper.toDto(comment);
            populateDtoWithImageUrls(comment, dto);
            return dto;

        } catch (Exception ex) {
            cleanupPartialUploads(
                    comment.getLargeImageFileKey() != null,
                    comment.getSmallImageFileKey() != null,
                    comment.getLargeImageFileKey(),
                    comment.getSmallImageFileKey()
            );
            throw ex instanceof RuntimeException
                    ? (RuntimeException) ex
                    : new RuntimeException(ex);
        }
    }

    @Transactional
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));
        CommentDto dto = commentMapper.toDto(comment);
        populateDtoWithImageUrls(comment, dto);
        return dto;
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));

        String largeKey = comment.getLargeImageFileKey();
        if (largeKey != null) {
            try {
                s3StorageService.deleteFile(largeKey);
            } catch (RuntimeException ex) {
                log.warn("Can not delete large image for (key={}): {}", largeKey, ex.getMessage());
            }
            comment.setLargeImageFileKey(null);
        }

        String smallKey = comment.getSmallImageFileKey();
        if (smallKey != null) {
            try {
                s3StorageService.deleteFile(smallKey);
            } catch (RuntimeException ex) {
                log.warn("Can not delete small image for (key={}): {}", smallKey, ex.getMessage());
            }
            comment.setSmallImageFileKey(null);
        }
        commentRepository.delete(comment);
    }

    private void populateDtoWithImageUrls(Comment comment, CommentDto dto) {
        String largeKey = comment.getLargeImageFileKey();
        if (largeKey != null) {
            dto.setUrlLarge(s3StorageService.generatePresignedUrl(largeKey));
        }
        String smallKey = comment.getSmallImageFileKey();
        if (smallKey != null) {
            dto.setUrlThumb(s3StorageService.generatePresignedUrl(smallKey));
        }
    }

    private void attachImageToComment(Comment comment, MultipartFile file) {

        String contentType = file.getContentType();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String originalName = (file.getOriginalFilename() != null)
                ? file.getOriginalFilename().toLowerCase()
                : "";
        String ext = imageService.getFileExtension(originalName);

        String largeKey = "comments/images/large/" + uuid + "_large." + ext;
        String smallKey = "comments/images/small/" + uuid + "_small." + ext;

        boolean largeUploaded = false;
        boolean smallUploaded = false;

        try {
            byte[] largeBytes = imageService.createLargeImage(file);
            try (InputStream isLarge = new ByteArrayInputStream(largeBytes)) {
                s3StorageService.uploadFile(
                        largeKey,
                        isLarge,
                        largeBytes.length,
                        imageService.getResizedImageContentType(contentType)
                );
            }
            largeUploaded = true;
            comment.setLargeImageFileKey(largeKey);

            byte[] smallBytes = imageService.createSmallImage(file);
            try (InputStream isSmall = new ByteArrayInputStream(smallBytes)) {
                s3StorageService.uploadFile(
                        smallKey,
                        isSmall,
                        smallBytes.length,
                        imageService.getResizedImageContentType(contentType)
                );
            }
            smallUploaded = true;
            comment.setSmallImageFileKey(smallKey);

        } catch (IOException ex) {
            cleanupPartialUploads(largeUploaded, smallUploaded, largeKey, smallKey);
            throw new CommentImageException("Error I/O while uploading images for comment", ex);

        } catch (RuntimeException ex) {
            cleanupPartialUploads(largeUploaded, smallUploaded, largeKey, smallKey);
            throw new CommentImageException("Cannot upload images for comment", ex);
        }
    }

    private void cleanupPartialUploads(
            boolean largeUploaded,
            boolean smallUploaded,
            String largeKey,
            String smallKey
    ) {
        if (smallUploaded) {
            try {
                s3StorageService.deleteFile(smallKey);
            } catch (RuntimeException ex) {
                log.warn("Cannot delete small image for (key={}): {}", smallKey, ex.getMessage());
            }
        }
        if (largeUploaded) {
            try {
                s3StorageService.deleteFile(largeKey);
            } catch (RuntimeException ex) {
                log.warn("Cannot delete large image for (key={}): {}", largeKey, ex.getMessage());
            }
        }
    }
}
