package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;

    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        Comment commentSaved = commentRepository.save(comment);

        return commentMapper.toDto(commentSaved);
    }

    @Transactional
    public CommentDto changeComment(CommentDto commentDto) {
        Comment commentFromDB = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new DataValidationException("couldn't find a comment by id: " + commentDto.getId()));

        commentFromDB.setContent(commentDto.getContent());

        return commentMapper.toDto(commentFromDB);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsOnPostId(long id) {
        commentValidator.getAllCommentsOnPostIdService(id);

        List<Comment> comments = commentRepository.findAllByPostId(id);
        return comments.stream().map(commentMapper::toDto).toList();
    }

    @Transactional
    public void deleteComment(long id) {
        commentRepository.deleteById(id);
    }
}
