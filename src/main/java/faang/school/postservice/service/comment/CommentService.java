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


    public CommentDto addNewComment(Long id, CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        Post post = getPostById(id);
        comment.setPost(post);
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    public CommentDto changeComment(CommentDto commentDto) {
        commentValidator.validateCommentAuthor(commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDTO(comment);
    }

    public CommentDto deleteComment(CommentDto commentDto) {
        commentRepository.deleteById(commentDto.getId());
        return commentDto;
    }

    public List<CommentDto> getAllComments(Long id) {
        List<Comment> allByPostId = commentRepository.findAllByPostId(id);
        return commentMapper.toDtoList(allByPostId);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There are no posts with that ID"));
    }
}
