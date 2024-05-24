package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static faang.school.postservice.exception.MessagesForCommentsException.*;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final PostMapper postMapper;

    public CommentDto createComment(long id, CommentDto commentDto) {
        checkPostId(id);
        checkCommentDto(commentDto);

        Comment comment = commentMapper.dtoToComment(commentDto);
        comment.setPost(postMapper.toEntity(postService.getPostById(id)));
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        Comment comment = returnCommentIfExists(commentDto);

        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    public List<CommentDto> getAllComments(long id) {
        checkPostId(id);
        List<Comment> comments = commentRepository.findAllByPostId(postService.getPostById(id).getId());
        if (comments == null) {
            throw new DataValidationException(NO_COMMENTS_IN_THE_POST.getMessage());
        }
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
        return commentMapper.commentsToCommentsDto(comments);
    }

    public void deleteComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        Comment comment = returnCommentIfExists(commentDto);
        commentRepository.delete(comment);
    }

    private void checkCommentDto(CommentDto commentDto) {
        if (userServiceClient.getUser(commentDto.getAuthorId()) == null) {
            throw new DataValidationException(NO_USER_IN_DB.getMessage());
        }
    }

    private Comment returnCommentIfExists(CommentDto commentDto) {
        if (commentDto.getId() == null) {
            throw new DataValidationException(ID_IS_NULL.getMessage());
        }
        return commentRepository.findById(commentDto.getId()).
                orElseThrow(() -> new DataValidationException(NO_COMMENT_IN_DB.getMessage()));
    }

    private void checkPostId(long id) {
        if (id <= 0) {
            throw new DataValidationException(POST_ID_IS_INCORRECT.getMessage());
        }
    }
}
