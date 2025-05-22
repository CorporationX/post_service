package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService{

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(long id) {
        return null;
    }

    @Override
    public List<CommentDto> getAllComments(CommentDto commentDto) {
        return null;
    }

    @Override
    public void deleteComment(long id) {

    }
}
