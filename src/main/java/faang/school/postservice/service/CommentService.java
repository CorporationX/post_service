package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentServiceValidator commentServiceValidator;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentDto createComment(long id, CommentDto commentDto) {
        Post post = commentServiceValidator.validateForCreateComment(id, commentDto);

        Comment comment = commentMapper.dtoToComment(commentDto);
        comment.setPost(post);
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentServiceValidator.validateForUpdateComment(commentDto);

        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);

        return commentMapper.commentToDto(comment);
    }

    public List<CommentDto> getAllComments(long id) {
        List<Comment> comments = commentServiceValidator.validateForGetAllComments(id);
        comments.sort(Comparator.comparing(Comment::getCreatedAt));
        return commentMapper.commentsToCommentsDto(comments);
    }

    public void deleteComment(CommentDto commentDto) {
        commentRepository.delete(commentServiceValidator.validateForDeleteComment(commentDto));
    }
}
