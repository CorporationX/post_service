package faang.school.postservice.service.comment;

import faang.school.postservice.annotation.CacheCreateComment;
import faang.school.postservice.annotation.CacheUpdateComment;
import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.comment.CommentResponseImageDto;
import faang.school.postservice.mapper.comment.MapperComment;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceFacade {
    private final MapperComment mapperComment;
    private final CommentService commentService;

    @CacheCreateComment
    public CommentDtoResponse createComment(long postId, String content) {
        Comment commentCreate = commentService.createComment(postId, content);

        return mapperComment.fromEntityToDto(commentCreate);
    }

    @CacheUpdateComment
    public CommentDtoResponse updateComment(long commentId, String content) {
        Comment commentUpdate = commentService.updateComment(commentId, content);

        return mapperComment.fromEntityToDto(commentUpdate);
    }

    public List<CommentDtoResponse> getAllComments(long postId) {
        List<Comment> comments = commentService.getAllComments(postId);

        return mapperComment.fromDtoListToEntityList(comments);
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
