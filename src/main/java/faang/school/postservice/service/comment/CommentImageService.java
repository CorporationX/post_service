package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.CommentImageDto;
import faang.school.postservice.exception.CommentImageException;
import faang.school.postservice.mapper.comment.CommentImageMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.image.ImageProcessingService;
import faang.school.postservice.service.s3.PresignService;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentImageService {

    private final S3Service s3Service;
    private final ImageProcessingService imageService;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentImageMapper commentImageMapper;
    private final PresignService presignService;

    @Transactional
    public CommentImageDto createCommentWithOptionalImage(
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
        comment = commentRepository.save(comment);

        String largeKey = null;
        String smallKey = null;
        boolean largeUploaded = false;
        boolean smallUploaded = false;

        try {
            if (file != null && !file.isEmpty()) {
                String originalName = file.getOriginalFilename() != null
                        ? file.getOriginalFilename().toLowerCase()
                        : "";
                String ext = imageService.getFileExtension(originalName);
                String contentType = imageService.getResizedImageContentType(file.getContentType());

                String basePath = "comments/" + postId + "/comments/" + comment.getId() + "/";
                String uuid = UUID.randomUUID().toString().replace("-", "");
                largeKey = basePath + "large_" + uuid + "." + ext;
                smallKey = basePath + "small_" + uuid + "." + ext;

                byte[] largeBytes = imageService.createLargeImage(file);
                s3Service.uploadBytesAsResource(largeBytes, largeKey, contentType);
                largeUploaded = true;
                comment.setLargeImageFileKey(largeKey);

                byte[] smallBytes = imageService.createSmallImage(file);
                s3Service.uploadBytesAsResource(smallBytes, smallKey, contentType);
                smallUploaded = true;
                comment.setSmallImageFileKey(smallKey);

                comment = commentRepository.save(comment);
            }

            CommentImageDto dto = commentImageMapper.toDto(comment);
            populateDtoWithImageUrls(comment, dto);
            return dto;

        } catch (IOException | RuntimeException ex) {
            cleanupPartialUploads(largeUploaded, smallUploaded, largeKey, smallKey);
            throw new CommentImageException("Error while uploading images for comment", ex);
        }
    }

    @Transactional(readOnly = true)
    public CommentImageDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));
        CommentImageDto dto = commentImageMapper.toDto(comment);
        populateDtoWithImageUrls(comment, dto);
        return dto;
    }

    @Transactional
    public void deleteCommentWithImage(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));

        String largeKey = comment.getLargeImageFileKey();
        if (largeKey != null) {
            try {
                s3Service.deleteFile(largeKey);
            } catch (RuntimeException ex) {
                log.warn("Cannot delete large image (key={}): {}", largeKey, ex.getMessage());
            }
            comment.setLargeImageFileKey(null);
        }
        String smallKey = comment.getSmallImageFileKey();
        if (smallKey != null) {
            try {
                s3Service.deleteFile(smallKey);
            } catch (RuntimeException ex) {
                log.warn("Cannot delete small image (key={}): {}", smallKey, ex.getMessage());
            }
            comment.setSmallImageFileKey(null);
        }
        commentRepository.delete(comment);
    }

    private void populateDtoWithImageUrls(Comment comment, CommentImageDto dto) {
        if (comment.getLargeImageFileKey() != null) {
            dto.setUrlLarge(presignService.generatePresignedUrl(comment.getLargeImageFileKey()));
        }
        if (comment.getSmallImageFileKey() != null) {
            dto.setUrlThumb(presignService.generatePresignedUrl(comment.getSmallImageFileKey()));
        }
    }

    private void cleanupPartialUploads(
            boolean largeUploaded, boolean smallUploaded,
            String largeKey, String smallKey) {
        if (smallUploaded && smallKey != null) {
            try {
                s3Service.deleteFile(smallKey);
            } catch (Exception e) {
                log.warn("Failed to cleanup small image (key={}): {}", smallKey, e.getMessage());
            }
        }
        if (largeUploaded && largeKey != null) {
            try {
                s3Service.deleteFile(largeKey);
            } catch (Exception e) {
                log.warn("Failed to cleanup large image (key={}): {}", largeKey, e.getMessage());
            }
        }
    }
}
