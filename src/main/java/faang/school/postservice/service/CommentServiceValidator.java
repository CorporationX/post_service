package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static faang.school.postservice.exception.MessagesForCommentsException.*;

@Component
@AllArgsConstructor
public class CommentServiceValidator {
    private UserServiceClient userServiceClient;
    private CommentRepository commentRepository;
    private CommentMapper commentMapper;
    private PostService postService;

    public Post validateForCreateComment(long id, CommentDto commentDto) {
        checkCommentDto(commentDto);
        return checkPostById(id);
    }

    public Comment validateForUpdateComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        return checkCommentRepositoryById(commentDto);
    }

    public List<Comment> validateForGetAllComments(long id) {
        List<Comment> comments = commentRepository.findAllByPostId(checkPostById(id).getId());
        if (comments == null) {
            throw new DataValidationException(NO_COMMENTS_IN_THE_POST.getMessage());
        } else return comments;
    }

    public Comment validateForDeleteComment(CommentDto commentDto) {
        checkCommentDto(commentDto);
        return checkCommentRepositoryById(commentDto);
    }

    private void checkCommentDto(CommentDto commentDto) {
        if (userServiceClient.getUser(commentDto.getAuthorId()) == null) {
            throw new DataValidationException(NO_USER_IN_DB.getMessage());
        }
    }

    private Comment checkCommentRepositoryById(CommentDto commentDto) {
        return commentRepository.findById(commentMapper.dtoToComment(commentDto).getId()).
                orElseThrow(() -> new DataValidationException(NO_COMMENT_IN_DB.getMessage()));
    }

    private Post checkPostById(long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            throw new DataValidationException(NO_POST_IN_DB.getMessage());
        }
        return post;
    }
}
