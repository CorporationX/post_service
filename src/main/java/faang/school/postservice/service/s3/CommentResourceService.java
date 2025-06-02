package faang.school.postservice.service.s3;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.CommentAlreadyHasPictureException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.PictureSize;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.S3Servce;
import faang.school.postservice.util.ImageResizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.AccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentResourceService {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final S3Servce s3Servce;
    private final ImageResizer imageResizer;

    @Value("${entity.commentServiceFileLimitMb}")
    private long fileLimitMb;

    @Transactional
    public List<ResourceDto> addResourceToComment(long commentId, MultipartFile file)
            throws FileSizeLimitExceededException, AccessException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        checkUserIsAuthor(userContext.getUserId(), comment);
        checkHasPictureAlready(comment);
        checkFileSize(file);
        Post post = comment.getPost();
        long postId = post.getId();
        String path = "Post" + postId + "Comment" + commentId;

        Resource largeImage = s3Servce.uploadFile
                (imageResizer.getResizedImageStream(file, PictureSize.LARGE), path + "Large");
        Resource smallImage = s3Servce.uploadFile
                (imageResizer.getResizedImageStream(file, PictureSize.SMALL), path + "Small");
        smallImage.setPost(post);
//        smallImage.setComment(comment);
        largeImage.setPost(post);
//        чтобы при получении поста в его ресурсах не значились картинки комментариев
//        largeImage.setComment(comment);

        resourceRepository.save(smallImage);
        resourceRepository.save(largeImage);

        comment.setSmallImageFileKey(smallImage.getKey());
        comment.setLargeImageFileKey(largeImage.getKey());
        commentRepository.save(comment);
        log.info("Uploading image for comment with id {} - Finished", commentId);
        return resourceMapper.toListDto(List.of(smallImage, largeImage));
    }

    public InputStream downloadFile(String fileKey) {
        resourceRepository.findByKey(fileKey).orElseThrow(() ->
                new EntityNotFoundException(String.format("No image with key %s", fileKey)));
        return s3Servce.downloadFile(fileKey);
    }

    @Transactional
    public void deleteImageFromCommentById(Long commentId) throws AccessException {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        checkUserIsAuthor(userContext.getUserId(), comment);
        String largeImageFileKey = comment.getLargeImageFileKey();
        String smallImageFileKey = comment.getSmallImageFileKey();
        s3Servce.deleteFile(largeImageFileKey);
        s3Servce.deleteFile(smallImageFileKey);
        resourceRepository.deleteByKey(largeImageFileKey);
        comment.setLargeImageFileKey(null);
        resourceRepository.deleteByKey(smallImageFileKey);
        comment.setSmallImageFileKey(null);

        log.info("Deleting image for comment with id {} - Finished", commentId);
    }

    public byte[] getCommentImage(Long commentId, PictureSize size) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        String key;
        if (size == PictureSize.SMALL) {
            key = comment.getSmallImageFileKey();
        } else {
            key = comment.getLargeImageFileKey();
        }
        InputStream imageInputStream = s3Servce.downloadFile(key);
        log.info("Getting image for comment with id {} - Finished", commentId);
        try {
            return imageInputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error reading image input stream", e);
            throw new RuntimeException("Failed to read image", e);
        } finally {
            try {
                imageInputStream.close();
            } catch (IOException e) {
                log.error("Error closing image input stream", e);
            }
        }
    }


    private void checkUserIsAuthor(long userId, Comment comment) throws AccessException {
        userServiceClient.getUser(userId);
        if (comment.getAuthorId() != userId) {
            throw new AccessException("Users can only add or remove images from their own comments");
        }
    }

    private void checkFileSize(MultipartFile file) throws FileSizeLimitExceededException {
        long fileLimitBytes = DataSize.ofMegabytes(fileLimitMb).toBytes();
        if (file.getSize() > fileLimitBytes) {
            throw new FileSizeLimitExceededException("The file must be smaller", file.getSize(), fileLimitBytes);
        }
    }

    private void checkHasPictureAlready(Comment comment) {
        if (comment.getLargeImageFileKey() != null || comment.getSmallImageFileKey() != null) {
            throw new CommentAlreadyHasPictureException
                    ("This comment already has a picture. Delete it before uploading new one");
        }
    }
}
