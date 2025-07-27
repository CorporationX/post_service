package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.amazonS3.ImageCompressor;
import faang.school.postservice.service.amazonS3.S3Service;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.CommentValidation;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class CommentService {
    private static final int MAX_SIZE_FOR_LARGE_IMAGE = 1080;
    private static final int MAX_SIZE_FOR_SMALL_IMAGE = 170;

    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;
    private final CommentValidation commentValidation;
    private final UserContext userContext;
    private final PostService postService;
    private final S3Service s3Service;
    private final ImageCompressor imageCompressor;

    @Transactional
    public Comment createComment(long postId, String content, long authorId) {
        Post post = postService.getPostById(postId);

        commentValidation.validateLengthContentComment(content);

        Comment comment = Comment.builder()
                .authorId(authorId)
                .post(post)
                .content(content)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment {} has been saved", savedComment);

        return savedComment;
    }

    @Transactional
    public Comment updateComment(long commentId, String newContent) {
        Comment comment = getComment(commentId);
        long authorId = userContext.getUserId();

        commentValidation.validateLengthContentComment(newContent);
        commentValidation.checkContentNotEquals(comment.getContent(), newContent);
        commentValidation.checkAuthorEqualsUser(authorId, comment.getAuthorId());

        comment.setContent(newContent);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> getAllComments(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        if (comments.isEmpty()) {
            commentValidation.validatePostExists(postId);
        }
        return comments;
    }

    @Transactional
    public void deleteComment(long commentId) {
        long userId = userContext.getUserId();
        Comment comment = getComment(commentId);

        commentValidation.checkAuthorEqualsUser(userId, comment.getAuthorId());

        deleteFile(commentId);
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.error("comment under the ID {} not found", commentId);
                    return new EntityNotFoundException("comment not found");
                });
    }

    @Transactional
    public void uploadFile(long commentId, MultipartFile originalFile) {
        Comment comment = getComment(commentId);

        if (!commentValidation.isKeyImageEmpty(comment)) {
            deleteFile(commentId);
        }

        String folderForLargeImage = String.format("largeImageForComment-%d", comment.getId());
        String folderForSmallImage = String.format("SmallImageForComment-%d", comment.getId());

        MultipartFile largeImage = imageCompressor.compressImage(originalFile, MAX_SIZE_FOR_LARGE_IMAGE);
        MultipartFile smallImage = imageCompressor.compressImage(originalFile, MAX_SIZE_FOR_SMALL_IMAGE);

        String keyLargeImage = s3Service.uploadFile(folderForLargeImage, largeImage);
        String keySmallImage = s3Service.uploadFile(folderForSmallImage, smallImage);

        comment.setLargeImageFileKey(keyLargeImage);
        comment.setSmallImageFileKey(keySmallImage);

        commentRepository.save(comment);
    }

    @Transactional
    public void deleteFile(long commentId) {
        Comment comment = getComment(commentId);
        String keyLargeImage = comment.getLargeImageFileKey();
        String keySmallImage = comment.getSmallImageFileKey();

        s3Service.deleteFile(keyLargeImage);
        s3Service.deleteFile(keySmallImage);
    }

    @Transactional
    public CommentResponseImageDto downloadSmallImage(long commentId) {
        Comment comment = getComment(commentId);
        String keySmallImage = comment.getSmallImageFileKey();

        return s3Service.downloadFile(keySmallImage);
    }

    @Transactional
    public CommentResponseImageDto downloadLargeImage(long commentId) {
        Comment comment = getComment(commentId);
        String keyLargeImage = comment.getLargeImageFileKey();

        return s3Service.downloadFile(keyLargeImage);
    }

    @Transactional(readOnly = true)
    public List<Comment> getNotVerifiedComments() {
        return commentRepository.findAllNotVerified();
    }

    @Transactional
    public List<Comment> saveVerifiedComments(List<Comment> comments) {
        return commentRepository.saveAll(comments);
    }

    @Transactional
    public int deleteCommentsWithProfanities() {
        return commentRepository.deleteAllFailedVerification();
    }
}