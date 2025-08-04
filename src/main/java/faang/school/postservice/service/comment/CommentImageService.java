package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.CommentImageDto;
import faang.school.postservice.exception.CommentImageException;
import faang.school.postservice.mapper.comment.CommentImageMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.adapter.CommentRepoAdapter;
import faang.school.postservice.repository.adapter.PostRepoAdapter;
import faang.school.postservice.service.image.ImageProcessingService;
import faang.school.postservice.service.s3.PresignService;
import faang.school.postservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentImageService {

    private final S3Service s3Service;
    private final ImageProcessingService imageService;
    private final CommentImageMapper commentImageMapper;
    private final PresignService presignService;
    private final CommentRepoAdapter commentRepoAdapter;
    private final PostRepoAdapter postRepoAdapter;

    @Transactional
    public CommentImageDto createCommentWithOptionalImage(
            String content,
            Long authorId,
            Long postId,
            MultipartFile file
    ) {
        Post post = postRepoAdapter.getById(postId);

        Comment comment = Comment.builder()
                .content(content)
                .authorId(authorId)
                .post(post)
                .build();
        comment = commentRepoAdapter.saveComment(comment);

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
                largeKey = String.format("%slarge_%s.%s", basePath, uuid, ext);
                smallKey = String.format("%ssmall_%s.%s", basePath, uuid, ext);

                byte[] largeBytes = imageService.createLargeImage(file);
                s3Service.uploadBytesAsResource(largeBytes, largeKey, contentType);
                largeUploaded = true;
                comment.setLargeImageFileKey(largeKey);

                byte[] smallBytes = imageService.createSmallImage(file);
                s3Service.uploadBytesAsResource(smallBytes, smallKey, contentType);
                smallUploaded = true;
                comment.setSmallImageFileKey(smallKey);

                comment = commentRepoAdapter.saveComment(comment);
            }

            CommentImageDto dto = commentImageMapper.toDto(comment);
            populateDtoWithImageUrls(comment, dto);
            return dto;

        } catch (IOException | RuntimeException ex) {
            log.error("Exception while uploading image for comment {}: ", comment.getId(), ex);
            cleanupPartialUploads(largeUploaded, smallUploaded, largeKey, smallKey);
            throw new CommentImageException("Error while uploading images for comment", ex);
        }
    }

    @Transactional(readOnly = true)
    public CommentImageDto getCommentById(Long id) {
        Comment comment = commentRepoAdapter.getById(id);
        CommentImageDto dto = commentImageMapper.toDto(comment);
        populateDtoWithImageUrls(comment, dto);
        return dto;
    }

    @Transactional
    public void deleteCommentWithImage(Long id) {
        Comment comment = commentRepoAdapter.getById(id);

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
        commentRepoAdapter.deleteComment(comment);
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
