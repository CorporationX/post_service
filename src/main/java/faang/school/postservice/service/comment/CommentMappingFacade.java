package faang.school.postservice.service.comment;

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
public class CommentMappingFacade {
    private final MapperComment mapperComment;
    private final CommentServiceFacade commentServiceFacade;

    public CommentDtoResponse createComment(long postId, String content) {
        Comment commentCreate = commentServiceFacade.createComment(postId, content);

        return mapperComment.fromEntityToDto(commentCreate);
    }

    public CommentDtoResponse updateComment(long commentId, String content) {
        Comment commentUpdate = commentServiceFacade.updateComment(commentId, content);

        return mapperComment.fromEntityToDto(commentUpdate);
    }

    public List<CommentDtoResponse> getAllComments(long postId) {
        List<Comment> comments = commentServiceFacade.getAllComments(postId);

        return mapperComment.fromDtoListToEntityList(comments);
    }

    public void deleteComment(long commentId) {
        commentServiceFacade.deleteComment(commentId);
    }

    public void uploadFile(long commentId, MultipartFile files) {
        commentServiceFacade.uploadFile(commentId, files);
    }

    public void deleteFile(long commentId) {
        commentServiceFacade.deleteFile(commentId);
    }

    public CommentResponseImageDto getSmallImage(long commentId) {
        return commentServiceFacade.getSmallImage(commentId);
    }

    public CommentResponseImageDto getLargeImage(long commentId) {
        return commentServiceFacade.getLargeImage(commentId);
    }
}
