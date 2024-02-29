package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;


    public CommentDto addNewComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = getPostById(postId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto.getId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDTO(savedComment);
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(allByPostId);
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("There are no posts with that id: " + postId));
    }
}
