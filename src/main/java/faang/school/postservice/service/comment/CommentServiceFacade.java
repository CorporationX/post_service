package faang.school.postservice.service.comment;

import faang.school.postservice.annotation.CommentCreationEventKafka;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.facade.comment.CommentEventPublisherFacade;
import faang.school.postservice.service.user.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceFacade {
    private final CommentService commentService;
    private final CommentImageService commentImageService;
    private final UserCacheService userCacheService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final CommentEventPublisherFacade commentEventPublisherFacade;

    @CommentCreationEventKafka
    public Comment createComment(long postId, String content) {
        long userId = userContext.getUserId();
        UserDto userDto = userServiceClient.getUserById(userId);

        Comment commentCreate = commentService.create(postId, content);

        userCacheService.saveUser(userDto);

        commentEventPublisherFacade.sendCommentMessage(commentCreate);

        return commentCreate;
    }

    public Comment updateComment(long commentId, String content) {
        return commentService.update(commentId, content);
    }

    public List<Comment> getAllComments(long postId) {
        return commentService.getAllByPostId(postId);
    }

    public void deleteComment(long commentId) {
        commentService.delete(commentId);
    }

    public void uploadFile(long commentId, MultipartFile files) {
        commentImageService.uploadImageForComment(commentId, files);
    }

    public void deleteFile(long commentId) {
        commentService.delete(commentId);
    }

    public ImageDownloadDto getSmallImage(long commentId) {
        return commentImageService.downloadPreviewByCommentId(commentId);
    }

    public ImageDownloadDto getLargeImage(long commentId) {
        return commentImageService.downloadImageByCommentId(commentId);
    }
}
