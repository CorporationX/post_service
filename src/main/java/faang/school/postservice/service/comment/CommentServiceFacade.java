package faang.school.postservice.service.comment;

import faang.school.postservice.annotation.CommentCreationEventKafka;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
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
    private final UserCacheService userCacheService;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @CommentCreationEventKafka
    public Comment createComment(long postId, String content) {
        long userId = userContext.getUserId();
        UserDto userDto = userServiceClient.getUser(userId);

        Comment commentCreate = commentService.createComment(postId, content, userId);

        userCacheService.saveUser(userDto);

        return commentCreate;
    }

    public Comment updateComment(long commentId, String content) {
        return commentService.updateComment(commentId, content);
    }

    public List<Comment> getAllComments(long postId) {
        return commentService.getAllComments(postId);
    }

    public void deleteComment(long commentId) {
        commentService.deleteComment(commentId);
    }

    public void uploadFile(long commentId, MultipartFile files) {
        commentService.uploadFile(commentId, files);
    }

    public void deleteFile(long commentId) {
        commentService.deleteFile(commentId);
    }

    public CommentResponseImageDto getSmallImage(long commentId) {
        return commentService.downloadSmallImage(commentId);
    }

    public CommentResponseImageDto getLargeImage(long commentId) {
        return commentService.downloadLargeImage(commentId);
    }
}
