package faang.school.postservice.facade.comment;

import faang.school.postservice.dto.comment.CommentDtoResponse;
import faang.school.postservice.dto.image.ImageDownloadDto;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.service.comment.CommentServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentMappingFacade {
    private final CommentMapper mapperComment;
    private final CommentServiceFacade commentServiceFacade;

    public CommentDtoResponse createComment(long postId, String content) {
        Comment commentCreate = commentServiceFacade.createComment(postId, content);

        return mapperComment.toDto(commentCreate);
    }

    public CommentDtoResponse updateComment(long commentId, String content) {
        Comment commentUpdate = commentServiceFacade.updateComment(commentId, content);

        return mapperComment.toDto(commentUpdate);
    }

    public List<CommentDtoResponse> getAllComments(long postId) {
        List<Comment> comments = commentServiceFacade.getAllComments(postId);

        return mapperComment.toDtoList(comments);
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

    public ImageDownloadDto getSmallImage(long commentId) {
        return commentServiceFacade.getSmallImage(commentId);
    }

    public ImageDownloadDto getLargeImage(long commentId) {
        return commentServiceFacade.getLargeImage(commentId);
    }
}
