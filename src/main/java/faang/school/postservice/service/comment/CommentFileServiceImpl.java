package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.CommentAlreadyHasPictureException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.PictureSize;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.CommentFileService;
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
public class CommentFileServiceImpl implements CommentFileService {

    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final ResourceRepository resourceRepository;
    private final S3Servce s3Servce;

    @Value("${entity.commentServiceFileLimitMb}")
    private long fileLimitMb;

    @Transactional
    @Override
    public List<String> addImageToComment(long commentId, MultipartFile file)
            throws FileSizeLimitExceededException, AccessException {
        checkFileSize(file);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        checkUserIsAuthor(userContext.getUserId(), comment);
        checkHasPictureAlready(comment);
        Post post = comment.getPost();
        long postId = post.getId();
        String path = "Post" + postId + "Comment" + commentId;

        String largeImageKey = s3Servce.uploadFile
                (ImageResizer.getResizedImageStream(file, PictureSize.LARGE), path + "Large");
        String smallImageKey = s3Servce.uploadFile
                (ImageResizer.getResizedImageStream(file, PictureSize.SMALL), path + "Small");
        comment.setSmallImageFileKey(smallImageKey);
        comment.setLargeImageFileKey(largeImageKey);
        commentRepository.save(comment);
        log.info("Uploading image for comment with id {} - Finished", commentId);
        return List.of(smallImageKey, largeImageKey);
    }

    @Transactional
    @Override
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

    @Override
    public byte[] getCommentImage(Long commentId, PictureSize size) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Comment with id %d does not exist", commentId)));
        String key;
        if (size == PictureSize.SMALL) {
            key = comment.getSmallImageFileKey();
        } else {
            key = comment.getLargeImageFileKey();
        }
        try (InputStream imageInputStream = s3Servce.downloadFile(key)) {
            log.info("Getting image for comment with id {} - Finished", commentId);
            return imageInputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Error reading image input stream", e);
            throw new RuntimeException("Failed to read image", e);
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
                    (String.format("This comment with id %d already has a picture. Delete it before uploading new one",
                            comment.getId()));
        }
    }
}
