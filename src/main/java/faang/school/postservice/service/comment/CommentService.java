package faang.school.postservice.service.comment;

import faang.school.postservice.dto.image.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.s3.S3StorageService;
import faang.school.postservice.service.image.ImageProcessingService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
    ) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        Comment comment = Comment.builder()
                .content(content)
                .authorId(authorId)
                .post(post)
                .build();
        comment = commentRepository.save(comment);

        if (file != null && !file.isEmpty()) {
            attachImageToComment(comment, file);
            comment = commentRepository.save(comment);
        }

        CommentDto dto = commentMapper.toDto(comment);
        if (comment.getLargeImageFileKey() != null) {
            dto.setUrlLarge(s3StorageService.generatePresignedUrl(comment.getLargeImageFileKey()));
        }
        if (comment.getSmallImageFileKey() != null) {
            dto.setUrlThumb(s3StorageService.generatePresignedUrl(comment.getSmallImageFileKey()));
        }
        return dto;
    }

    private void attachImageToComment(Comment comment, MultipartFile file) throws IOException {
        imageService.validateImage(file);

        String contentType = file.getContentType();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String originalName = file.getOriginalFilename();
        String ext = imageService.getFileExtension((originalName != null) ?
                originalName.toLowerCase() : "");

        String largeKey = "comments/images/large/" + uuid + "_large." + ext;
        String smallKey = "comments/images/small/" + uuid + "_small." + ext;

        boolean largeUploaded = false;
        try {
            byte[] largeBytes = imageService.createLargeImage(file);
            try (InputStream isLarge = new ByteArrayInputStream(largeBytes)) {
                s3StorageService.uploadFile(
                        largeKey,
                        isLarge,
                        largeBytes.length,
                        imageService.getResizedImageContentType(contentType));
            }
            largeUploaded = true;

            byte[] smallBytes = imageService.createSmallImage(file);
            try (InputStream isSmall = new ByteArrayInputStream(smallBytes)) {
                s3StorageService.uploadFile(
                        smallKey,
                        isSmall,
                        smallBytes.length,
                        imageService.getResizedImageContentType(contentType));
            }

            comment.setLargeImageFileKey(largeKey);
            comment.setSmallImageFileKey(smallKey);

        } catch (IOException | RuntimeException e) {
            if (largeUploaded) {
                s3StorageService.deleteFile(largeKey);
            }
            throw e;
        }
    }

    @Transactional
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));
        CommentDto dto = commentMapper.toDto(comment);

        if (comment.getLargeImageFileKey() != null) {
            dto.setUrlLarge(s3StorageService.generatePresignedUrl(comment.getLargeImageFileKey()));
        }
        if (comment.getSmallImageFileKey() != null) {
            dto.setUrlThumb(s3StorageService.generatePresignedUrl(comment.getSmallImageFileKey()));
        }
        return dto;
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found: " + id));

        if (comment.getLargeImageFileKey() != null) {
            s3StorageService.deleteFile(comment.getLargeImageFileKey());
        }
        if (comment.getSmallImageFileKey() != null) {
            s3StorageService.deleteFile(comment.getSmallImageFileKey());
        }
        commentRepository.delete(comment);
    }
}
